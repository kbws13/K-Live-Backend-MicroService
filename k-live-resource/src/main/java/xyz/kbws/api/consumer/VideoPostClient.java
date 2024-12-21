package xyz.kbws.api.consumer;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import xyz.kbws.model.entity.VideoFilePost;

/**
 * @author kbws
 * @date 2024/12/21
 * @description:
 */
@FeignClient(name = "k-live-web")
public interface VideoPostClient {

    @PostMapping("/inner/videoPost/transferVideoFile")
    void transferVideoFile(@RequestBody VideoFilePost videoFilePost);
}
