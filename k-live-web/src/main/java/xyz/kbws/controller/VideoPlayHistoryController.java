package xyz.kbws.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.kbws.annotation.AuthCheck;
import xyz.kbws.common.BaseResponse;
import xyz.kbws.common.ResultUtils;
import xyz.kbws.constant.CommonConstant;
import xyz.kbws.model.dto.videoPlayHistory.VideoHistoryQueryRequest;
import xyz.kbws.model.entity.VideoPlayHistory;
import xyz.kbws.model.vo.UserVO;
import xyz.kbws.redis.RedisComponent;
import xyz.kbws.service.VideoPlayHistoryService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author kbws
 * @date 2024/12/21
 * @description: 播放历史接口
 */
@Api(tags = "播放历史接口")
@RestController
@RequestMapping("/history")
public class VideoPlayHistoryController {
    @Resource
    private VideoPlayHistoryService videoPlayHistoryService;

    @Resource
    private RedisComponent redisComponent;

    @ApiOperation(value = "查询观看历史记录")
    @AuthCheck
    @PostMapping("/loadHistory")
    public BaseResponse<Page<VideoPlayHistory>> loadHistory(VideoHistoryQueryRequest videoHistoryQueryRequest, HttpServletRequest request) {
        String token = request.getHeader("token");
        UserVO userVO = redisComponent.getUserVO(token);
        long current = videoHistoryQueryRequest.getCurrent();
        long pageSize = videoHistoryQueryRequest.getPageSize();
        videoHistoryQueryRequest.setQueryVideoInfo(true);
        videoHistoryQueryRequest.setSortField("lastUpdateTime");
        videoHistoryQueryRequest.setSortOrder(CommonConstant.SORT_ORDER_DESC);
        videoHistoryQueryRequest.setUserId(userVO.getId());
        Page<VideoPlayHistory> page = new Page<>(current, pageSize);
        List<VideoPlayHistory> record = videoPlayHistoryService.selectList(videoHistoryQueryRequest);
        page.setRecords(record);
        page.setTotal(record.size());
        return ResultUtils.success(page);
    }

    @ApiOperation(value = "清空历史记录")
    @AuthCheck
    @GetMapping("/cleanHistory")
    public BaseResponse<Boolean> cleanHistory(HttpServletRequest request) {
        String token = request.getHeader("token");
        UserVO userVO = redisComponent.getUserVO(token);
        QueryWrapper<VideoPlayHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userVO.getId());
        boolean remove = videoPlayHistoryService.remove(queryWrapper);
        return ResultUtils.success(remove);
    }

    @ApiOperation(value = "删除历史记录")
    @AuthCheck
    @PostMapping("/deleteHistory")
    public BaseResponse<Boolean> deleteHistory(@NotNull String videoId, HttpServletRequest request) {
        String token = request.getHeader("token");
        UserVO userVO = redisComponent.getUserVO(token);
        QueryWrapper<VideoPlayHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userVO.getId())
                .eq("videoId", videoId);
        boolean remove = videoPlayHistoryService.remove(queryWrapper);
        return ResultUtils.success(remove);
    }
}
