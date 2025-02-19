package xyz.kbws.api.provider;

import org.springframework.web.bind.annotation.*;
import xyz.kbws.model.dto.videoPost.VideoPostQueryRequest;
import xyz.kbws.model.entity.VideoFilePost;
import xyz.kbws.model.entity.VideoPost;
import xyz.kbws.model.vo.VideoPostVO;
import xyz.kbws.service.VideoPostService;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author kbws
 * @date 2024/12/21
 * @description:
 */
@RestController
@RequestMapping("/inner/videoPost")
public class VideoPostApi {

    @Resource
    private VideoPostService videoPostService;

    @PostMapping("/transferVideoFile")
    public void transferVideoFile(@RequestBody VideoFilePost videoFilePost) {
        videoPostService.transferVideoFile(videoFilePost);
    }

    @PostMapping("/selectById")
    public VideoPost selectById(String videoId) {
        return videoPostService.getById(videoId);
    }

    @PostMapping("/loadVideoPost")
    List<VideoPostVO> loadVideoPost(@RequestBody VideoPostQueryRequest videoPostQueryRequest) {
        String userId = videoPostQueryRequest.getUserId();
        return videoPostService.loadVideoPost(videoPostQueryRequest, userId);
    }

    @PostMapping("/auditVideo")
    void auditVideo(String videoId, Integer status, String reason) {
        videoPostService.auditVideo(videoId, status, reason);
    }
}
