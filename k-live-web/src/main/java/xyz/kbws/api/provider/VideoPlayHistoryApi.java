package xyz.kbws.api.provider;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xyz.kbws.service.VideoPlayHistoryService;

import javax.annotation.Resource;

/**
 * @author kbws
 * @date 2024/12/22
 * @description:
 */
@RestController
@RequestMapping("/inner/videoPlayHistory")
public class VideoPlayHistoryApi {

    @Resource
    private VideoPlayHistoryService videoPlayHistoryService;

    @PostMapping("/saveHistory")
    void saveHistory(@RequestParam String userId, @RequestParam String videoId, @RequestParam Integer fileIndex) {
        videoPlayHistoryService.saveHistory(userId, videoId, fileIndex);
    }
}
