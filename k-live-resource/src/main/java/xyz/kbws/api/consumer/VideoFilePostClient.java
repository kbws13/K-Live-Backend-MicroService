package xyz.kbws.api.consumer;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import xyz.kbws.model.entity.VideoFilePost;

/**
 * @author kbws
 * @date 2025/3/28
 * @description:
 */
@FeignClient(name = "k-live-web", contextId = "videoFilePostClient")
public interface VideoFilePostClient {
    @PostMapping("/inner/videoFilePost/getVideoFilePostById")
    VideoFilePost getVideoFileById(@RequestParam String videoFileId);
}
