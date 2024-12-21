package xyz.kbws.controller;

import cn.hutool.core.bean.BeanUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.kbws.annotation.AuthCheck;
import xyz.kbws.common.BaseResponse;
import xyz.kbws.common.ResultUtils;
import xyz.kbws.constant.UserConstant;
import xyz.kbws.model.dto.comment.CommentLoadRequest;
import xyz.kbws.model.dto.danmu.DanmuLoadRequest;

import javax.annotation.Resource;

/**
 * @author kbws
 * @date 2024/12/21
 * @description: 互动管理接口
 */
@Api(tags = "互动管理接口")
@RestController
@RequestMapping("/interact")
public class InteractController {
    @Resource
    private VideoCommentService videoCommentService;

    @Resource
    private DanmuService danmuService;

    @ApiOperation(value = "查询评论")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/loadComment")
    public BaseResponse<Page<VideoComment>> loadComment(CommentLoadRequest commentLoadRequest) {
        long current = commentLoadRequest.getCurrent();
        long pageSize = commentLoadRequest.getPageSize();
        VideoCommentQuery videoCommentQuery = new VideoCommentQuery();
        videoCommentQuery.setVideoNameFuzzy(commentLoadRequest.getVideoName());
        videoCommentQuery.setQueryVideoInfo(true);
        videoCommentQuery.setCurrent(current);
        videoCommentQuery.setPageSize(pageSize);
        videoCommentQuery.setSortField("id desc");
        Page<VideoComment> page = new Page<>();
        List<VideoComment> videoComments = videoCommentService.listByParams(videoCommentQuery);
        page.setCurrent(current);
        page.setSize(pageSize);
        page.setTotal(videoComments.size());
        page.setRecords(videoComments);
        return ResultUtils.success(page);
    }

    @ApiOperation(value = "删除评论")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/deleteComment")
    public BaseResponse<Boolean> deleteComment(@NotNull Integer commentId) {
        boolean remove = videoCommentService.deleteComment(commentId, null);
        return ResultUtils.success(remove);
    }

    @ApiOperation(value = "查询弹幕")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/loadDanmu")
    public BaseResponse<Page<Danmu>> loadDanmu(@RequestBody DanmuLoadRequest danmuLoadRequest) {
        DanmuQuery danmuQuery = new DanmuQuery();
        BeanUtil.copyProperties(danmuLoadRequest, danmuQuery);
        List<Danmu> danmuList = danmuService.selectListByParam(danmuQuery);
        long current = danmuLoadRequest.getCurrent();
        long pageSize = danmuLoadRequest.getPageSize();
        Page<Danmu> page = new Page<>();
        page.setCurrent(current);
        page.setSize(pageSize);
        page.setTotal(danmuList.size());
        page.setRecords(danmuList);
        return ResultUtils.success(page);
    }

    @ApiOperation(value = "删除弹幕")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/deleteDanmu")
    public void deleteDanmu(@NotNull Integer danmuId) {
        danmuService.deleteDanmu(null, danmuId);
    }
}