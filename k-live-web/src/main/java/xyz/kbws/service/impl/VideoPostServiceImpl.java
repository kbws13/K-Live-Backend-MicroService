package xyz.kbws.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import xyz.kbws.common.ErrorCode;
import xyz.kbws.config.SystemSetting;
import xyz.kbws.constant.CommonConstant;
import xyz.kbws.constant.MqConstant;
import xyz.kbws.constant.UserConstant;
import xyz.kbws.es.EsComponent;
import xyz.kbws.exception.BusinessException;
import xyz.kbws.mapper.UserMapper;
import xyz.kbws.mapper.VideoFilePostMapper;
import xyz.kbws.mapper.VideoPostMapper;
import xyz.kbws.model.dto.videoPost.VideoPostQueryRequest;
import xyz.kbws.model.entity.*;
import xyz.kbws.model.enums.VideoFileTransferResultEnum;
import xyz.kbws.model.enums.VideoFileTypeEnum;
import xyz.kbws.model.enums.VideoStatusEnum;
import xyz.kbws.model.vo.VideoPostVO;
import xyz.kbws.rabbitmq.MessageProducer;
import xyz.kbws.redis.RedisComponent;
import xyz.kbws.service.VideoFilePostService;
import xyz.kbws.service.VideoFileService;
import xyz.kbws.service.VideoPostService;
import xyz.kbws.service.VideoService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author fangyuan
 * @description 针对表【videoPost(已发布视频信息表)】的数据库操作Service实现
 * @createDate 2024-11-28 20:36:20
 */
@Slf4j
@Service
public class VideoPostServiceImpl extends ServiceImpl<VideoPostMapper, VideoPost>
        implements VideoPostService {

    @Lazy
    @Resource
    private VideoService videoService;

    @Resource
    private VideoFileService videoFileService;

    @Resource
    private VideoFilePostService videoFilePostService;

    @Resource
    private VideoPostMapper videoPostMapper;

    @Resource
    private VideoFilePostMapper videoFilePostMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private EsComponent esComponent;

    @Resource
    private MessageProducer messageProducer;

    @GlobalTransactional(rollbackFor = Exception.class)
    @Override
    public void addVideoPost(VideoPost videoPost, List<VideoFilePost> videoFilePosts) {
        if (videoFilePosts.size() > redisComponent.getSystemSetting().getVideoCount()) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        videoPost.setId(RandomUtil.randomNumbers(UserConstant.LENGTH_10));
        videoPost.setStatus(VideoStatusEnum.STATUS0.getValue());
        this.save(videoPost);

        int index = 1;
        for (VideoFilePost videoFilePost : videoFilePosts) {
            videoFilePost.setFileIndex(index++);
            videoFilePost.setVideoId(videoPost.getId());
            videoFilePost.setUserId(videoPost.getUserId());
            videoFilePost.setFileId(RandomUtil.randomString(CommonConstant.LENGTH_20));
            videoFilePost.setUpdateType(VideoFileTypeEnum.UPDATE.getValue());
            videoFilePost.setTransferResult(VideoFileTransferResultEnum.TRANSFER.getValue());
        }
        videoFilePostService.saveOrUpdateBatch(videoFilePosts);

        for (VideoFilePost item : videoFilePosts) {
            item.setUserId(videoPost.getUserId());
            item.setVideoId(videoPost.getId());
            // 发送视频转码消息到消息队列
            String jsonStr = JSONUtil.toJsonStr(item);
            messageProducer.sendMessage(MqConstant.TRANSFER_VIDEO_QUEUE, jsonStr);
        }
    }

    @GlobalTransactional(rollbackFor = Exception.class)
    @Override
    public void updateVideoPost(VideoPost videoPost, List<VideoFilePost> videoFilePosts) {
        if (videoFilePosts.size() > redisComponent.getSystemSetting().getVideoCount()) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        VideoPost post = this.getById(videoPost.getId());
        if (post == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "该视频不存在");
        }
        if (ArrayUtils.contains(new Integer[]{VideoStatusEnum.STATUS0.getValue(), VideoStatusEnum.STATUS2.getValue()}, post.getStatus())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        List<VideoFilePost> deleteFileList = new ArrayList<>();
        List<VideoFilePost> addFileList;
        QueryWrapper<VideoFilePost> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("videoId", videoPost.getId());
        queryWrapper.eq("userId", post.getUserId());
        List<VideoFilePost> dbVideoFilePosts = videoFilePostService.list(queryWrapper);
        Map<String, VideoFilePost> uploadFileMap = videoFilePosts.stream()
                .collect(Collectors.toMap(
                        VideoFilePost::getUploadId,
                        Function.identity(),
                        (data1, data2) -> data2
                ));
        boolean changeFileName = false;
        for (VideoFilePost item : dbVideoFilePosts) {
            VideoFilePost updateFile = uploadFileMap.get(item.getUploadId());
            if (updateFile == null) {
                deleteFileList.add(item);
            } else if (!updateFile.getFileName().equals(item.getFileName())) {
                changeFileName = true;
            }
        }
        addFileList = videoFilePosts.stream()
                .filter(item -> item.getFileId() == null)
                .collect(Collectors.toList());
        boolean changeVideo = this.changeVideo(videoPost);
        if (!addFileList.isEmpty()) {
            videoPost.setStatus(VideoStatusEnum.STATUS0.getValue());
        } else if (changeVideo || changeFileName) {
            videoPost.setStatus(VideoStatusEnum.STATUS2.getValue());
        }
        this.updateById(videoPost);
        if (!deleteFileList.isEmpty()) {
            List<String> delFileIdList = deleteFileList.stream().map(VideoFilePost::getFileId).collect(Collectors.toList());
            videoFilePostMapper.deleteBatchByFileId(delFileIdList, videoPost.getUserId());
            List<String> delFilePathList = deleteFileList.stream().map(VideoFilePost::getFilePath).collect(Collectors.toList());
            // 发送删除文件消息到消息队列
            JSONArray jsonArray = JSONUtil.parseArray(delFilePathList);
            messageProducer.sendMessage(MqConstant.DEL_VIDEO_QUEUE, jsonArray.toString());
        }
        int index = 1;
        for (VideoFilePost videoFilePost : videoFilePosts) {
            videoFilePost.setFileIndex(index++);
            videoFilePost.setVideoId(videoPost.getId());
            videoFilePost.setUserId(videoPost.getUserId());
            if (videoFilePost.getFileId() == null) {
                videoFilePost.setFileId(RandomUtil.randomString(CommonConstant.LENGTH_20));
                videoFilePost.setUpdateType(VideoFileTypeEnum.UPDATE.getValue());
                videoFilePost.setTransferResult(VideoFileTransferResultEnum.TRANSFER.getValue());
            }
        }
        videoFilePostService.saveOrUpdateBatch(videoFilePosts);
        if (!addFileList.isEmpty()) {
            for (VideoFilePost item : addFileList) {
                item.setUserId(videoPost.getUserId());
                item.setVideoId(videoPost.getId());
                String jsonStr = JSONUtil.toJsonStr(item);
                // 发送视频转码消息到消息队列
                messageProducer.sendMessage(MqConstant.TRANSFER_VIDEO_QUEUE, jsonStr);
            }
        }
    }

    @Override
    public void transferVideoFile(VideoFilePost videoFilePost) {
        log.info("即将开始 videoFilePost: {}", JSONUtil.toJsonStr(videoFilePost));
        videoFilePostMapper.updateById(videoFilePost);
        // 查询是否有转码失败的
        QueryWrapper<VideoFilePost> queryWrapper = new QueryWrapper<>();
        UpdateWrapper<VideoPost> updateWrapper = new UpdateWrapper<>();
        queryWrapper.eq("videoId", videoFilePost.getVideoId());
        queryWrapper.eq("transferResult", VideoFileTransferResultEnum.FAIL.getValue());
        long failCount = videoFilePostService.count(queryWrapper);
        if (failCount > 0) {
            updateWrapper.eq("id", videoFilePost.getVideoId());
            updateWrapper.set("status", VideoStatusEnum.STATUS1.getValue());
            this.update(updateWrapper);
        }
        queryWrapper.eq("transferResult", VideoFileTransferResultEnum.TRANSFER.getValue());
        long transferCount = videoFilePostService.count(queryWrapper);
        if (transferCount == 0) {
            Integer duration = videoFilePostMapper.sumDuration(videoFilePost.getVideoId());
            updateWrapper.set("duration", duration);
            updateWrapper.set("status", VideoStatusEnum.STATUS2.getValue());
            this.update(updateWrapper);
        }
    }

    @Override
    public List<VideoPostVO> loadVideoPost(VideoPostQueryRequest videoPostQueryRequest, String userId) {
        User user = userMapper.selectById(userId);
        return videoPostMapper.loadVideoPost(videoPostQueryRequest, userId, user.getUserRole().equals("admin"));
    }

    @GlobalTransactional(rollbackFor = Exception.class)
    @Override
    public void auditVideo(String videoId, Integer status, String reason) {
        VideoStatusEnum videoStatusEnum = VideoStatusEnum.getEnumByValue(status);
        if (videoStatusEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "该参数不存在");
        }
        UpdateWrapper<VideoPost> queryWrapper = new UpdateWrapper<>();
        queryWrapper.eq("status", VideoStatusEnum.STATUS2.getValue());
        queryWrapper.eq("id", videoId);
        queryWrapper.set("status", videoStatusEnum.getValue());
        int auditCount = videoPostMapper.update(null, queryWrapper);
        if (auditCount == 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "审核失败，请稍后重试");
        }
        UpdateWrapper<VideoFilePost> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("videoId", videoId);
        updateWrapper.set("updateType", VideoFileTypeEnum.NO_UPDATE.getValue());
        videoFilePostService.update(updateWrapper);

        if (videoStatusEnum == VideoStatusEnum.STATUS4) {
            return;
        }
        VideoPost videoPost = this.getById(videoId);
        Video dbVideo = videoService.getById(videoId);
        if (dbVideo == null) {
            // 第一次过审
            dbVideo = new Video();
            SystemSetting systemSetting = redisComponent.getSystemSetting();
            // 给用户加硬币
            userMapper.updateCoinCount(videoPost.getUserId(), systemSetting.getPostVideoCoinCount());
        }
        // 更新发布信息到正式表
        BeanUtil.copyProperties(videoPost, dbVideo);
        videoService.saveOrUpdate(dbVideo);
        // 更新视频信息到正式表 先删除再添加
        QueryWrapper<VideoFile> fileQueryWrapper = new QueryWrapper<>();
        fileQueryWrapper.eq("videoId", videoId);
        List<VideoFile> deleteFileList = videoFileService.list(fileQueryWrapper);
        videoFileService.remove(fileQueryWrapper);

        QueryWrapper<VideoFilePost> videoFilePostQueryWrapper = new QueryWrapper<>();
        videoFilePostQueryWrapper.eq("videoId", videoId);
        List<VideoFilePost> list = videoFilePostService.list(videoFilePostQueryWrapper);
        List<VideoFile> videoFiles = BeanUtil.copyToList(list, VideoFile.class);
        videoFileService.saveBatch(videoFiles);

        // 删除文件
        List<String> delFilePathList = deleteFileList.stream().map(VideoFile::getFilePath).collect(Collectors.toList());
        if (!delFilePathList.isEmpty()) {
            // 发送删除文件消息到消息队列
            for (String filePath : delFilePathList) {
                messageProducer.sendMessage(MqConstant.DEL_VIDEO_QUEUE, filePath);
            }
        }
        // 保存信息到 ES
        esComponent.saveDoc(dbVideo);
    }

    private Boolean changeVideo(VideoPost videoPost) {
        VideoPost dbPost = this.getById(videoPost.getId());
        String introduction = Optional.ofNullable(dbPost.getIntroduction()).orElse("");
        // 标题、封面、标签、简介
        return !videoPost.getName().equals(dbPost.getName())
                || !videoPost.getCover().equals(dbPost.getCover())
                || !videoPost.getTags().equals(dbPost.getTags())
                || !videoPost.getIntroduction().equals(introduction);
    }
}




