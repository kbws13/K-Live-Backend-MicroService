package xyz.kbws.api.provider;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.kbws.model.entity.VideoComment;
import xyz.kbws.model.query.VideoCommentQuery;
import xyz.kbws.service.VideoCommentService;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author kbws
 * @date 2024/12/22
 * @description:
 */
@RestController
@RequestMapping("/inner/videoComment")
public class VideoCommentApi {

    @Resource
    private VideoCommentService videoCommentService;

    @PostMapping("/listByParams")
    List<VideoComment> listByParams(VideoCommentQuery videoCommentQuery) {
        return videoCommentService.listByParams(videoCommentQuery);
    }

    @PostMapping("deleteComment")
    Boolean deleteComment(Integer commentId, String userId) {
        return videoCommentService.deleteComment(commentId, null);
    }

    @PostMapping("/deleteVideoComment")
    void deleteVideoComment(QueryWrapper<VideoComment> videoCommentQueryWrapper) {
        videoCommentService.remove(videoCommentQueryWrapper);
    }
}
