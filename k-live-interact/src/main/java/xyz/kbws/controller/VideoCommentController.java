package xyz.kbws.controller;

import cn.hutool.core.bean.BeanUtil;
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
import xyz.kbws.common.BaseResponse;
import xyz.kbws.common.ResultUtils;
import xyz.kbws.constant.CommonConstant;
import xyz.kbws.model.dto.comment.CommentAddRequest;
import xyz.kbws.model.dto.comment.CommentLoadRequest;
import xyz.kbws.model.entity.VideoComment;
import xyz.kbws.model.enums.MessageTypeEnum;
import xyz.kbws.model.query.VideoCommentQuery;
import xyz.kbws.model.vo.UserVO;
import xyz.kbws.model.vo.VideoCommentResultVO;
import xyz.kbws.redis.RedisComponent;
import xyz.kbws.service.VideoCommentService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.List;

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
    private RedisComponent redisComponent;

    @ApiOperation(value = "加载所有评论")
    @AuthCheck
    @PostMapping("/loadAllComment")
    public BaseResponse<Page<VideoComment>> loadComment(@RequestBody VideoCommentQuery videoCommentQuery, HttpServletRequest request) {
        String token = request.getHeader("token");
        UserVO userVO = redisComponent.getUserVO(token);
        videoCommentQuery.setSortField("id");
        videoCommentQuery.setSortOrder(CommonConstant.SORT_ORDER_DESC);
        videoCommentQuery.setUserId(userVO.getId());
        Page<VideoComment> page = new Page<>();
        List<VideoComment> videoComments = videoCommentService.listByParams(videoCommentQuery);
        page.setRecords(videoComments);
        page.setTotal(videoComments.size());
        page.setCurrent(videoCommentQuery.getCurrent());
        page.setSize(videoCommentQuery.getPageSize());
        return ResultUtils.success(page);
    }

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
        String token = request.getHeader("token");
        UserVO userVO = redisComponent.getUserVO(token);
        VideoCommentResultVO videoCommentResultVO = videoCommentService.loadComment(commentLoadRequest, userVO);
        return ResultUtils.success(videoCommentResultVO);
    }

    @ApiOperation(value = "置顶评论")
    @AuthCheck
    @PostMapping("/topComment")
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
}
