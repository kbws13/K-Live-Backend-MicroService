package xyz.kbws.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.kbws.model.entity.VideoComment;
import xyz.kbws.model.query.VideoCommentQuery;

import java.util.List;

/**
 * @author fangyuan
 * @description 针对表【videoComment(评论表)】的数据库操作Service
 * @createDate 2024-12-07 12:14:12
 */
public interface VideoCommentService extends IService<VideoComment> {

    VideoComment addComment(VideoComment videoComment, Integer replyCommentId);

    List<VideoComment> listByParams(VideoCommentQuery query);

    Boolean topComment(Integer commentId, String userId);

    Boolean cancelTopComment(Integer commentId, String userId);

    Boolean deleteComment(Integer commentId, String userId);
}
