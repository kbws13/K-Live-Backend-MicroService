package xyz.kbws.api.consumer;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import xyz.kbws.model.entity.Video;
import xyz.kbws.model.enums.SearchOrderTypeEnum;

/**
 * @author kbws
 * @date 2024/12/21
 * @description:
 */
@FeignClient(name = "k-live-web", contextId = "videoClient")
public interface VideoClient {

    @PostMapping("/inner/video/selectById")
    Video selectById(@RequestParam String videoId);

    @PostMapping("/inner/video/updateCountInfo")
    void updateCountInfo(@RequestParam String videoId, @RequestParam String field, @RequestParam Integer changeCount);

    @PostMapping("/inner/video/updateDocCount")
    void updateDocCount(@RequestParam String videoId, @RequestParam SearchOrderTypeEnum searchOrderTypeEnum, @RequestParam Integer changeCount);

    @PostMapping("/inner/video/addPlayCount")
    void addPlayCount(@RequestParam String videoId);
}
