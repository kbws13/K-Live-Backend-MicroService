package xyz.kbws.api.provider;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.web.bind.annotation.*;
import xyz.kbws.model.entity.VideoFile;
import xyz.kbws.model.entity.VideoFilePost;
import xyz.kbws.service.VideoFilePostService;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author kbws
 * @date 2024/12/22
 * @description:
 */
@RestController
@RequestMapping("/inner/videoFilePost")
public class VideoFilePostApi {

    @Resource
    private VideoFilePostService videoFilePostService;

    @PostMapping("/getVideoFilePostById")
    public VideoFilePost getVideoById(@NotEmpty String videoFileId) {
        return videoFilePostService.getById(videoFileId);
    }

    @PostMapping("/selectVideoFileList")
    List<VideoFilePost> selectVideoFileList(@RequestParam("videoId") String videoId) {
        QueryWrapper<VideoFilePost> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("videoId", videoId)
                .orderByAsc("fileIndex");
        return videoFilePostService.list(queryWrapper);
    }
}
