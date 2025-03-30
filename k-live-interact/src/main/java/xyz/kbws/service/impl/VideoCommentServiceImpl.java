package xyz.kbws.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.stereotype.Service;
import xyz.kbws.api.consumer.UserClient;
import xyz.kbws.api.consumer.VideoClient;
import xyz.kbws.common.ErrorCode;
import xyz.kbws.constant.UserConstant;
import xyz.kbws.exception.BusinessException;
import xyz.kbws.mapper.VideoCommentMapper;
import xyz.kbws.model.entity.User;
import xyz.kbws.model.entity.Video;
import xyz.kbws.model.entity.VideoComment;
import xyz.kbws.model.enums.CommentTopTypeEnum;
import xyz.kbws.model.enums.UserActionTypeEnum;
import xyz.kbws.model.query.VideoCommentQuery;
import xyz.kbws.service.VideoCommentService;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author fangyuan
 * @description 针对表【videoComment(评论表)】的数据库操作Service实现
 * @createDate 2024-12-07 12:14:12
 */
@Service
public class VideoCommentServiceImpl extends ServiceImpl<VideoCommentMapper, VideoComment>
        implements VideoCommentService {

    @Resource
    private VideoCommentMapper videoCommentMapper;

    @Resource
    private VideoClient videoClient;

    @Resource
    private UserClient userClient;

    @GlobalTransactional(rollbackFor = Exception.class)
    @Override
    public VideoComment addComment(VideoComment videoComment, Integer replyCommentId) {
        Video video = videoClient.selectById(videoComment.getVideoId());
        if (video == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "该视频不存在");
        }
        if (video.getInteraction() != null && !video.getInteraction().contains(UserConstant.ONE.toString())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "UP 主已关闭评论区");
        }
        if (replyCommentId != null) {
            VideoComment replyComment = this.getById(replyCommentId);
            if (replyComment == null || !replyComment.getVideoId().equals(videoComment.getVideoId())) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR);
            }
            if (replyComment.getParentCommentId() == 0) {
                videoComment.setParentCommentId(replyComment.getId());
            } else {
                videoComment.setParentCommentId(replyComment.getId());
                videoComment.setReplyUserId(replyComment.getUserId());
            }
            User user = userClient.selectById(replyComment.getUserId());
            videoComment.setReplyNickName(user.getNickName());
            videoComment.setReplyAvatar(user.getAvatar());
        } else {
            videoComment.setParentCommentId(0);
        }
        videoComment.setPostTime(DateUtil.date());
        videoComment.setVideoUserId(video.getUserId());
        this.save(videoComment);
        // 更新视频评论数量
        videoClient.updateCountInfo(videoComment.getVideoId(), UserActionTypeEnum.VIDEO_COMMENT.getField(), 1);
        return videoComment;
    }

    @Override
    public List<VideoComment> listByParams(VideoCommentQuery query) {
        if (query.getLoadChildren() != null && query.getLoadChildren()) {
            return videoCommentMapper.selectListWithChildren(query);
        }
        return videoCommentMapper.selectList(query);
    }

    @GlobalTransactional(rollbackFor = Exception.class)
    @Override
    public Boolean topComment(Integer commentId, String userId) {
        this.cancelTopComment(commentId, userId);
        VideoComment comment = this.getById(commentId);
        comment.setTopType(CommentTopTypeEnum.TOP.getValue());
        return this.updateById(comment);
    }

    @Override
    public Boolean cancelTopComment(Integer commentId, String userId) {
        VideoComment comment = this.getById(commentId);
        if (comment == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        Video video = videoClient.selectById(comment.getVideoId());
        if (video == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        if (!video.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        comment.setTopType(CommentTopTypeEnum.NOT_TOP.getValue());
        return this.updateById(comment);
    }

    @Override
    public Boolean deleteComment(Integer commentId, String userId) {
        VideoComment comment = this.getById(commentId);
        if (comment == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "该评论不存在");
        }
        Video video = videoClient.selectById(comment.getVideoId());
        if (video == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "评论所在视频不存在");
        }
        if (userId != null && !video.getUserId().equals(userId) && !comment.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "你没有权限删除");
        }
        boolean res1 = this.removeById(commentId);
        boolean res2 = true;
        if (comment.getParentCommentId() == 0) {
            // 删除二级评论
            QueryWrapper<VideoComment> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("parentCommentId", commentId);
            int changeCount = (int) this.count(queryWrapper);
            videoClient.updateCountInfo(video.getId(), UserActionTypeEnum.VIDEO_COMMENT.getField(), -changeCount);
            res2 = this.remove(queryWrapper);
        }
        return res1 & res2;
    }
}




