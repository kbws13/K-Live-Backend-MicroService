package xyz.kbws.api.consumer;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import xyz.kbws.model.entity.VideoPost;

/**
 * @author kbws
 * @date 2024/12/21
 * @description:
 */
@FeignClient(name = "k-live-web", contextId = "videoPostClient")
public interface VideoPostClient {

    @PostMapping("/inner/videoPost/selectById")
    VideoPost selectById(@RequestParam String videoId);
}
