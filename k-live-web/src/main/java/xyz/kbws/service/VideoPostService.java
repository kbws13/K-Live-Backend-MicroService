package xyz.kbws.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.kbws.model.entity.VideoFilePost;
import xyz.kbws.model.entity.VideoPost;

import java.util.List;

/**
 * @author fangyuan
 * @description 针对表【videoPost(已发布视频信息表)】的数据库操作Service
 * @createDate 2024-11-28 20:36:20
 */
public interface VideoPostService extends IService<VideoPost> {

    void addVideoPost(VideoPost videoPost, List<VideoFilePost> videoFilePosts);

    void updateVideoPost(VideoPost videoPost, List<VideoFilePost> videoFilePosts);

    void transferVideoFile(VideoFilePost videoFilePost);

    void auditVideo(String videoId, Integer status, String reason);
}
