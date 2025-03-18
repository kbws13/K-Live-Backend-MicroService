package xyz.kbws.controller;

import cn.hutool.json.JSONUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RList;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.web.bind.annotation.*;
import xyz.kbws.annotation.AuthCheck;
import xyz.kbws.common.BaseResponse;
import xyz.kbws.common.ResultUtils;
import xyz.kbws.constant.UserConstant;
import xyz.kbws.entity.ThreadPoolConfigEntity;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author kbws
 * @date 2025/3/18
 * @description:
 */
@Slf4j
@Api(tags = "动态线程池接口")
@RestController
@RequestMapping("/dynamicThreadPool")
public class DynamicThreadPoolController {

    @Resource
    private RedissonClient redissonClient;

    @ApiOperation(value = "获取线程池数据")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @GetMapping("/getThreadPoolList")
    public BaseResponse<List<ThreadPoolConfigEntity>> getThreadPoolList() {
        RList<ThreadPoolConfigEntity> cacheList = redissonClient.getList("THREAD_POOL_CONFIG_LIST_KEY");
        return ResultUtils.success(cacheList);
    }

    @ApiOperation(value = "查询线程池配置")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @GetMapping("/getThreadPoolConfig")
    public BaseResponse<ThreadPoolConfigEntity> getThreadPoolConfig(@RequestParam String applicationName, @RequestParam String threadPoolName) {
        String cacheKey = "THREAD_POOL_CONFIG_PARAMETER_LIST_KEY" + "_" + applicationName + "_" + threadPoolName;
        ThreadPoolConfigEntity entity = redissonClient.<ThreadPoolConfigEntity>getBucket(cacheKey).get();
        return ResultUtils.success(entity);
    }

    @ApiOperation(value = "修改线程池配置")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/updateThreadPoolConfig")
    public BaseResponse<Boolean> updateThreadPoolConfig(@RequestBody ThreadPoolConfigEntity entity) {
        log.info("修改线程池配置 {} {} {}", entity.getApplicationName(), entity.getThreadPoolName(), JSONUtil.toJsonStr(entity));
        RTopic topic = redissonClient.getTopic("DYNAMIC_THREAD_POOL_REDIS_TOPIC" + "_" + entity.getApplicationName());
        topic.publish(entity);
        log.info("线程池修改配置完成");
        return ResultUtils.success(true);
    }
}
