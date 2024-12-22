package xyz.kbws.api.consumer;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author kbws
 * @date 2024/12/22
 * @description:
 */
@FeignClient(name = "k-live-web", contextId = "videoPlayHistoryClient")
public interface VideoPlayHistoryClient {

    @PostMapping("/inner/videoPlayHistory/saveHistory")
    void saveHistory(@RequestParam String userId, @RequestParam String videoId, @RequestParam Integer fileIndex);
}
