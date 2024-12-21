package xyz.kbws.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.kbws.annotation.AuthCheck;
import xyz.kbws.annotation.RecordMessage;
import xyz.kbws.api.consumer.WebClient;
import xyz.kbws.common.BaseResponse;
import xyz.kbws.common.ResultUtils;
import xyz.kbws.constant.UserConstant;
import xyz.kbws.model.dto.videoPost.VideoPostAuditRequest;
import xyz.kbws.model.dto.videoPost.VideoPostQueryRequest;
import xyz.kbws.model.entity.VideoFilePost;
import xyz.kbws.model.enums.MessageTypeEnum;
import xyz.kbws.model.vo.UserVO;
import xyz.kbws.model.vo.VideoPostVO;
import xyz.kbws.redis.RedisComponent;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author kbws
 * @date 2024/12/22
 * @description: 视频接口
 */
@Api(tags = "视频接口")
@RestController
@RequestMapping("/video")
public class VideoController {

    @Resource
    private WebClient webClient;

    @Resource
    private RedisComponent redisComponent;

    @ApiOperation(value = "查询稿件接口")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/loadVideoPost")
    public BaseResponse<Page<VideoPostVO>> loadVideoPost(@RequestBody VideoPostQueryRequest videoPostQueryRequest,
                                                         HttpServletRequest request) {
        String token = request.getHeader("token");
        UserVO userVO = redisComponent.getUserVO(token);
        List<VideoPostVO> record = webClient.loadVideoPost(videoPostQueryRequest, userVO.getId());
        Page<VideoPostVO> res = new Page<>();
        res.setRecords(record);
        res.setCurrent(videoPostQueryRequest.getCurrent());
        res.setSize(videoPostQueryRequest.getPageSize());
        res.setSize(record.size());
        return ResultUtils.success(res);
    }

    @ApiOperation(value = "视频审核接口")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @RecordMessage(messageType = MessageTypeEnum.SYSTEM)
    @PostMapping("/auditVideoPost")
    public void auditVideoPost(@RequestBody VideoPostAuditRequest videoPostAuditRequest) {
        String videoId = videoPostAuditRequest.getVideoId();
        Integer status = videoPostAuditRequest.getStatus();
        String reason = videoPostAuditRequest.getReason();
        webClient.auditVideo(videoId, status, reason);
    }

    @ApiOperation(value = "设置推荐视频接口")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/recommendVideo")
    public BaseResponse<Boolean> recommendVideo(@NotEmpty String videoId) {
        Boolean res = webClient.recommendVideo(videoId);
        return ResultUtils.success(res);
    }

    @ApiOperation(value = "删除视频接口")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/deleteVideo")
    public void deleteVideo(@NotEmpty String videoId) {
        webClient.deleteVideo(videoId, null);
    }

    @ApiOperation(value = "查询视频分 p 接口")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/loadVideoPList")
    public BaseResponse<List<VideoFilePost>> loadVideoPList(@NotEmpty String videoId) {
        QueryWrapper<VideoFilePost> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("videoId", videoId)
                .orderByAsc("fileIndex");
        List<VideoFilePost> videoFilePostList = webClient.selectVideoFileList(queryWrapper);
        return ResultUtils.success(videoFilePostList);
    }
}
