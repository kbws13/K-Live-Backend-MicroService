package xyz.kbws.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import xyz.kbws.annotation.AuthCheck;
import xyz.kbws.common.BaseResponse;
import xyz.kbws.common.ResultUtils;
import xyz.kbws.config.SystemSetting;
import xyz.kbws.redis.RedisComponent;

import javax.annotation.Resource;

/**
 * @author kbws
 * @date 2024/12/22
 * @description: 系统设置接口
 */
@Api(tags = "系统设置接口")
@RestController
@RequestMapping("/system")
public class SystemController {

    @Resource
    private RedisComponent redisComponent;

    @ApiOperation(value = "获取系统设置")
    @AuthCheck
    @GetMapping("/getSystemSetting")
    public BaseResponse<SystemSetting> getSystemSetting() {
        SystemSetting systemSetting = redisComponent.getSystemSetting();
        return ResultUtils.success(systemSetting);
    }

    @ApiOperation(value = "保存系统设置")
    @AuthCheck
    @PostMapping("/saveSystemSetting")
    public BaseResponse<String> saveSystemSetting(@RequestBody SystemSetting systemSetting) {
        redisComponent.saveSystemSetting(systemSetting);
        return ResultUtils.success("保存成功");
    }
}
