package xyz.kbws.api.provider;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.kbws.model.entity.VideoFilePost;
import xyz.kbws.service.VideoPostService;

import javax.annotation.Resource;

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
    public void transferVideoFile(VideoFilePost videoFilePost) {
        videoPostService.transferVideoFile(videoFilePost);
    }
}
