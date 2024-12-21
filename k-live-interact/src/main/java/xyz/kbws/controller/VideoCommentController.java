package xyz.kbws.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.kbws.annotation.AuthCheck;
import xyz.kbws.annotation.RecordMessage;
import xyz.kbws.api.consumer.VideoClient;
import xyz.kbws.common.BaseResponse;
import xyz.kbws.common.ErrorCode;
import xyz.kbws.common.ResultUtils;
import xyz.kbws.constant.UserConstant;
import xyz.kbws.exception.BusinessException;
import xyz.kbws.model.dto.comment.CommentAddRequest;
import xyz.kbws.model.dto.comment.CommentLoadRequest;
import xyz.kbws.model.entity.Action;
import xyz.kbws.model.entity.Video;
import xyz.kbws.model.entity.VideoComment;
import xyz.kbws.model.enums.CommentTopTypeEnum;
import xyz.kbws.model.enums.MessageTypeEnum;
import xyz.kbws.model.enums.UserActionTypeEnum;
import xyz.kbws.model.query.VideoCommentQuery;
import xyz.kbws.model.vo.UserVO;
import xyz.kbws.model.vo.VideoCommentResultVO;
import xyz.kbws.redis.RedisComponent;
import xyz.kbws.service.ActionService;
import xyz.kbws.service.VideoCommentService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author kbws
 * @date 2024/12/21
 * @description:
 */
@Api(tags = "评论接口")
@Slf4j
@RestController
@RequestMapping("/comment")
public class VideoCommentController {

    @Resource
    private VideoCommentService videoCommentService;

    @Resource
    private ActionService actionService;

    @Resource
    private VideoClient videoClient;

    @Resource
    private RedisComponent redisComponent;

    @ApiOperation(value = "发布评论")
    @AuthCheck
    @RecordMessage(messageType = MessageTypeEnum.COMMENT)
    @PostMapping("/addComment")
    public BaseResponse<VideoComment> addComment(@RequestBody CommentAddRequest commentAddRequest, HttpServletRequest request) {
        String token = request.getHeader("token");
        UserVO userVO = redisComponent.getUserVO(token);
        VideoComment videoComment = new VideoComment();
        BeanUtil.copyProperties(commentAddRequest, videoComment);
        videoComment.setUserId(userVO.getId());
        VideoComment res = videoCommentService.addComment(videoComment, commentAddRequest.getReplyCommentId());
        return ResultUtils.success(res);
    }

    @ApiOperation(value = "加载评论")
    @PostMapping("/loadComment")
    public BaseResponse<VideoCommentResultVO> loadComment(@RequestBody CommentLoadRequest commentLoadRequest, HttpServletRequest request) {
        Video video = videoClient.selectById(commentLoadRequest.getVideoId());
        if (video.getInteraction() != null && video.getInteraction().contains(UserConstant.ZERO.toString())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "UP 主已关闭评论区");
        }
        VideoCommentResultVO videoCommentResultVO = new VideoCommentResultVO();
        long current = commentLoadRequest.getCurrent();
        long pageSize = commentLoadRequest.getPageSize();
        VideoCommentQuery videoCommentQuery = new VideoCommentQuery();
        videoCommentQuery.setVideoId(commentLoadRequest.getVideoId());
        videoCommentQuery.setParentCommandId(0);
        videoCommentQuery.setLoadChildren(true);
        videoCommentQuery.setCurrent(current);
        videoCommentQuery.setPageSize(pageSize);
        Integer orderType = commentLoadRequest.getOrderType();
        if (orderType == null || orderType == 0) {
            videoCommentQuery.setSortField("likeCount desc, id desc");
        } else {
            videoCommentQuery.setSortField("id desc");
        }

        Page<VideoComment> page = new Page<>();
        List<VideoComment> videoComments = videoCommentService.listByParams(videoCommentQuery);
        if (current == 1) {
            List<VideoComment> topCommentList = topComment(videoCommentQuery.getVideoId());
            if (!topCommentList.isEmpty()) {
                List<VideoComment> commentList = videoComments.stream()
                        .filter(item -> !item.getId().equals(topCommentList.get(0).getId()))
                        .collect(Collectors.toList());
                commentList.addAll(topCommentList);
                videoComments = commentList;
            }
        }
        page.setCurrent(current);
        page.setSize(pageSize);
        page.setTotal(videoComments.size());
        page.setRecords(videoComments);
        // 获取用户点赞、投币、收藏
        List<Action> list = new ArrayList<>();
        String token = request.getHeader("token");
        UserVO userVO = redisComponent.getUserVO(token);
        if (userVO != null) {
            QueryWrapper<Action> query = new QueryWrapper<>();
            List<Integer> types = Arrays.asList(UserActionTypeEnum.COMMENT_LIKE.getValue(), UserActionTypeEnum.COMMENT_HATE.getValue());
            query.eq("videoId", commentLoadRequest.getVideoId())
                    .eq("userId", userVO.getId())
                    .in("actionType", types);
            list = actionService.list(query);
        }
        videoCommentResultVO.setPage(page);
        videoCommentResultVO.setActionList(list);
        return ResultUtils.success(videoCommentResultVO);
    }

    @ApiOperation(value = "置顶评论")
    @AuthCheck
    @PostMapping("/top")
    public BaseResponse<Boolean> topComment(@NotNull Integer commentId, HttpServletRequest request) {
        String token = request.getHeader("token");
        UserVO userVO = redisComponent.getUserVO(token);
        boolean res = videoCommentService.topComment(commentId, userVO.getId());
        return ResultUtils.success(res);
    }

    @ApiOperation(value = "取消置顶")
    @AuthCheck
    @PostMapping("/cancelTop")
    public BaseResponse<Boolean> cancelTopComment(@NotNull Integer commentId, HttpServletRequest request) {
        String token = request.getHeader("token");
        UserVO userVO = redisComponent.getUserVO(token);
        boolean res = videoCommentService.cancelTopComment(commentId, userVO.getId());
        return ResultUtils.success(res);
    }

    @ApiOperation(value = "删除评论")
    @AuthCheck
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteComment(@NotNull Integer commentId, HttpServletRequest request) {
        String token = request.getHeader("token");
        UserVO userVO = redisComponent.getUserVO(token);
        boolean res = videoCommentService.deleteComment(commentId, userVO.getId());
        return ResultUtils.success(res);
    }

    private List<VideoComment> topComment(String videoId) {
        VideoCommentQuery videoCommentQuery = new VideoCommentQuery();
        videoCommentQuery.setVideoId(videoId);
        videoCommentQuery.setTopType(CommentTopTypeEnum.TOP.getValue());
        videoCommentQuery.setLoadChildren(true);
        return videoCommentService.listByParams(videoCommentQuery);
    }
}
