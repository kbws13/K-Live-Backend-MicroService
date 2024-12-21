package xyz.kbws.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.kbws.common.BaseResponse;
import xyz.kbws.common.ResultUtils;
import xyz.kbws.model.dto.video.VideoReportRequest;
import xyz.kbws.redis.RedisComponent;

import javax.annotation.Resource;

/**
 * @author kbws
 * @date 2024/12/21
 * @description:
 */
@Api(tags = "在线接口")
@RestController
@RequestMapping("/online")
public class OnlineController {

    @Resource
    private RedisComponent redisComponent;

    @ApiOperation(value = "上报在线观看人数")
    @PostMapping("/reportVideoPlayOnline")
    public BaseResponse<Integer> repostVideoPlayOnline(@RequestBody VideoReportRequest videoReportRequest) {
        Integer count = redisComponent.reportVideoPlayOnline(videoReportRequest.getFileId(), videoReportRequest.getDeviceId());
        return ResultUtils.success(count);
    }
}
