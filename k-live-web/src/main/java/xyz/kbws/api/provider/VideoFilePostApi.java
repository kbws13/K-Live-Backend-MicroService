package xyz.kbws.api.provider;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.kbws.model.entity.VideoFilePost;
import xyz.kbws.service.VideoFilePostService;

import javax.annotation.Resource;
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

    @PostMapping("/selectVideoFileList")
    List<VideoFilePost> selectVideoFileList(QueryWrapper<VideoFilePost> queryWrapper) {
        return videoFilePostService.list(queryWrapper);
    }
}
