package xyz.kbws.service.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.stereotype.Service;
import xyz.kbws.api.consumer.InteractClient;
import xyz.kbws.common.ErrorCode;
import xyz.kbws.config.SystemSetting;
import xyz.kbws.constant.MqConstant;
import xyz.kbws.es.EsComponent;
import xyz.kbws.exception.BusinessException;
import xyz.kbws.mapper.UserMapper;
import xyz.kbws.mapper.VideoMapper;
import xyz.kbws.model.dto.video.VideoQueryRequest;
import xyz.kbws.model.entity.*;
import xyz.kbws.model.enums.UserActionTypeEnum;
import xyz.kbws.model.enums.VideoRecommendTypeEnum;
import xyz.kbws.rabbitmq.MessageProducer;
import xyz.kbws.redis.RedisComponent;
import xyz.kbws.service.VideoFilePostService;
import xyz.kbws.service.VideoPostService;
import xyz.kbws.service.VideoService;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author fangyuan
 * @description 针对表【video(视频信息表)】的数据库操作Service实现
 * @createDate 2024-11-28 20:36:09
 */
@Service
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video>
        implements VideoService {

    @Resource
    private VideoPostService videoPostService;

    @Resource
    private VideoFilePostService videoFilePostService;

    @Resource
    private InteractClient interactClient;

    @Resource
    private VideoMapper videoMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private EsComponent esComponent;

    @Resource
    private MessageProducer messageProducer;

    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    @GlobalTransactional(rollbackFor = Exception.class)
    @Override
    public void deleteVideo(String videoId, String userId) {
        VideoPost videoPost = videoPostService.getById(videoId);
        if (videoPost == null || userId != null && !videoPost.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        this.removeById(videoId);
        videoPostService.removeById(videoId);
        // 减硬币
        SystemSetting systemSetting = redisComponent.getSystemSetting();
        userMapper.updateCoinCount(videoPost.getUserId(), -systemSetting.getPostVideoCoinCount());
        // 删除 ES 信息
        esComponent.deleteDoc(videoId);
        executorService.execute(() -> {
            // 删除分 p
            QueryWrapper<VideoFilePost> videoFilePostQueryWrapper = new QueryWrapper<>();
            videoFilePostQueryWrapper.eq("videoId", videoId);
            videoFilePostService.remove(videoFilePostQueryWrapper);
            // 删除弹幕
            QueryWrapper<Danmu> danmuQueryWrapper = new QueryWrapper<>();
            danmuQueryWrapper.eq("videoId", videoId);
            interactClient.deleteDanmu(danmuQueryWrapper);
            // 删除评论
            interactClient.deleteVideoComment(videoId);

            List<VideoFilePost> videoFilePostList = videoFilePostService.list(videoFilePostQueryWrapper);
            for (VideoFilePost videoFilePost : videoFilePostList) {
                JSONArray jsonArray = JSONUtil.parseArray(videoFilePost);
                messageProducer.sendMessage(MqConstant.DEL_VIDEO_QUEUE, jsonArray.toString());
            }
        });
    }

    @GlobalTransactional(rollbackFor = Exception.class)
    @Override
    public void changeInteraction(String videoId, String userId, String interaction) {
        Video video = new Video();
        video.setInteraction(interaction);
        QueryWrapper<Video> videoQueryWrapper = new QueryWrapper<>();
        videoQueryWrapper.eq("id", videoId)
                .eq("userId", userId);
        this.update(video, videoQueryWrapper);

        VideoPost videoPost = new VideoPost();
        videoPost.setInteraction(interaction);
        QueryWrapper<VideoPost> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", videoId)
                .eq("userId", userId);
        videoPostService.update(videoPost, queryWrapper);
    }

    @Override
    public List<Video> selectList(VideoQueryRequest videoQueryRequest) {
        return videoMapper.queryList(videoQueryRequest);
    }

    @Override
    public void addPlayCount(String videoId) {
        videoMapper.updateCountInfo(videoId, UserActionTypeEnum.VIDEO_PLAY.getField(), 1);
    }

    @Override
    public Boolean recommendVideo(String videoId) {
        Video video = this.getById(videoId);
        if (video == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        int recommendType;
        if (video.getRecommendType().equals(VideoRecommendTypeEnum.NO_RECOMMEND.getValue())) {
            recommendType = VideoRecommendTypeEnum.RECOMMEND.getValue();
        } else {
            recommendType = VideoRecommendTypeEnum.NO_RECOMMEND.getValue();
        }
        video.setRecommendType(recommendType);
        return this.updateById(video);
    }

    @Override
    public void updateCountInfo(String videoId, String field, Integer changeCount) {
        videoMapper.updateCountInfo(videoId, field, changeCount);
    }
}




