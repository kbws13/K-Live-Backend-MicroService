package xyz.kbws.api.provider;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.kbws.model.entity.VideoFile;
import xyz.kbws.service.VideoFileService;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;

/**
 * @author kbws
 * @date 2024/12/21
 * @description:
 */
@RestController
@RequestMapping("/inner/videoFile")
public class VideoFileApi {

    @Resource
    private VideoFileService videoFileService;

    @PostMapping("/getVideoFileById")
    public VideoFile getVideoById(@NotEmpty String videoFileId) {
        return videoFileService.getById(videoFileId);
    }
}
