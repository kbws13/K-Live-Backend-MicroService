package xyz.kbws.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.kbws.common.ErrorCode;
import xyz.kbws.config.AppConfig;
import xyz.kbws.config.SystemSetting;
import xyz.kbws.constant.CommonConstant;
import xyz.kbws.constant.FileConstant;
import xyz.kbws.constant.MqConstant;
import xyz.kbws.constant.UserConstant;
import xyz.kbws.es.EsComponent;
import xyz.kbws.exception.BusinessException;
import xyz.kbws.mapper.UserMapper;
import xyz.kbws.mapper.VideoFilePostMapper;
import xyz.kbws.mapper.VideoPostMapper;
import xyz.kbws.model.entity.Video;
import xyz.kbws.model.entity.VideoFile;
import xyz.kbws.model.entity.VideoFilePost;
import xyz.kbws.model.entity.VideoPost;
import xyz.kbws.model.enums.VideoFileTransferResultEnum;
import xyz.kbws.model.enums.VideoFileTypeEnum;
import xyz.kbws.model.enums.VideoStatusEnum;
import xyz.kbws.model.vo.UploadingFileVO;
import xyz.kbws.rabbitmq.MessageProducer;
import xyz.kbws.redis.RedisComponent;
import xyz.kbws.service.VideoFilePostService;
import xyz.kbws.service.VideoFileService;
import xyz.kbws.service.VideoPostService;
import xyz.kbws.service.VideoService;
import xyz.kbws.utils.FFmpegUtil;

import javax.annotation.Resource;
import java.io.File;
import java.io.RandomAccessFile;
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
    private VideoFilePostMapper videoFilePostMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private EsComponent esComponent;

    @Resource
    private MessageProducer messageProducer;

    @Resource
    private FFmpegUtil fFmpegUtil;

    @Resource
    private AppConfig appConfig;

    @Transactional(rollbackFor = Exception.class)
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
        }
        // 发送视频转码消息到消息队列
        JSONArray jsonArray = JSONUtil.parseArray(videoFilePosts);
        messageProducer.sendMessage(MqConstant.FILE_EXCHANGE_NAME, MqConstant.TRANSFER_VIDEO_ROOTING_KEY, jsonArray.toString());
    }

    @Transactional(rollbackFor = Exception.class)
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
        List<VideoFilePost> addFileList = new ArrayList<>();
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
            messageProducer.sendMessage(MqConstant.FILE_EXCHANGE_NAME, MqConstant.DEL_FILE_ROUTING_KEY, jsonArray.toString());
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
                messageProducer.sendMessage(MqConstant.FILE_EXCHANGE_NAME, MqConstant.TRANSFER_VIDEO_ROOTING_KEY, jsonStr);
            }
        }
    }

    @Override
    public void transferVideoFile(VideoFilePost videoFilePost) {
        try {
            UploadingFileVO uploadVideoFile = redisComponent.getUploadVideoFile(videoFilePost.getUserId(), videoFilePost.getUploadId());
            String tempFilePath = appConfig.getProjectFolder() + FileConstant.FILE_FOLDER + FileConstant.FILE_FOLDER_TEMP + uploadVideoFile.getFilePath();
            String targetFilePath = appConfig.getProjectFolder() + FileConstant.FILE_FOLDER + FileConstant.FILE_VIDEO + uploadVideoFile.getFilePath();
            FileUtil.copy(tempFilePath, targetFilePath, false);
            // 删除临时目录
            FileUtil.del(tempFilePath);
            // 删除 Redis 记录
            redisComponent.deleteUploadVideoFile(videoFilePost.getUserId(), videoFilePost.getUploadId());
            // 合并文件
            String completeVideo = targetFilePath + FileConstant.TEMP_VIDEO_NAME;
            this.union(targetFilePath, completeVideo, true);
            // 获取播放时长
            Integer duration = fFmpegUtil.getVideoDuration(completeVideo);
            videoFilePost.setDuration(duration);
            videoFilePost.setFileSize(new File(completeVideo).length());
            videoFilePost.setFilePath(FileConstant.FILE_VIDEO + uploadVideoFile.getFilePath());
            videoFilePost.setTransferResult(VideoFileTransferResultEnum.SUCCESS.getValue());
            // 将视频转成 TS 分片
            this.coverVideo2TS(completeVideo);
        } catch (Exception e) {
            log.error("文件转码失败: {}", e.getMessage());
            videoFilePost.setTransferResult(VideoFileTransferResultEnum.FAIL.getValue());
        } finally {
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
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void auditVideo(String videoId, Integer status, String reason) {
        VideoStatusEnum videoStatusEnum = VideoStatusEnum.getEnumByValue(status);
        if (videoStatusEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "该参数不存在");
        }
        QueryWrapper<VideoPost> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", VideoStatusEnum.STATUS2.getValue());
        queryWrapper.eq("id", videoId);
        int auditCount = Math.toIntExact(this.count(queryWrapper));
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
            JSONArray jsonArray = JSONUtil.parseArray(delFilePathList);
            messageProducer.sendMessage(MqConstant.FILE_EXCHANGE_NAME, MqConstant.DEL_FILE_ROUTING_KEY, jsonArray.toString());
        }
        // 保存信息到 ES
        esComponent.saveDoc(dbVideo);
    }

    private void coverVideo2TS(String completeVideo) {
        File voideFile = new File(completeVideo);
        File tsFolder = voideFile.getParentFile();
        String codec = fFmpegUtil.getVideoCodec(completeVideo);
        if (FileConstant.VIDEO_CODE_HEVC.equals(codec)) {
            String tempFileName = completeVideo + FileConstant.VIDEO_TEMP_FILE_SUFFIX;
            new File(completeVideo).renameTo(new File(tempFileName));
            fFmpegUtil.coverHevc2Mp4(tempFileName, completeVideo);
            FileUtil.del(tempFileName);
        }
        fFmpegUtil.coverVideo2TS(tsFolder, completeVideo);
        FileUtil.del(voideFile);
    }

    private void union(String dirPath, String toFilePath, Boolean delSource) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "目录不存在");
        }
        File[] fileList = dir.listFiles();
        File targetFile = new File(toFilePath);
        try (RandomAccessFile writeFile = new RandomAccessFile(targetFile, "rw")) {
            byte[] bytes = new byte[1024 * 10];
            for (int i = 0; i < fileList.length; i++) {
                int len = -1;
                // 创建读块文件的对象
                File chunkFile = new File(dirPath + File.separator + i);
                try (RandomAccessFile readFile = new RandomAccessFile(chunkFile, "r")) {
                    while ((len = readFile.read(bytes)) != -1) {
                        writeFile.write(bytes, 0, len);
                    }
                } catch (Exception e) {
                    log.error("合并分片失败: {}", e.getMessage());
                    throw new BusinessException(ErrorCode.OPERATION_ERROR, "合并分片失败" + e.getMessage());
                }
            }
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "合并文件 " + dirPath + " 出错了");
        } finally {
            if (delSource) {
                for (int i = 0; i < fileList.length; i++) {
                    fileList[i].delete();
                }
            }
        }
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




