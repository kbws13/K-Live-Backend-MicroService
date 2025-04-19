package xyz.kbws.controller;

import cn.hutool.json.JSONUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RList;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.web.bind.annotation.*;
import xyz.kbws.common.BaseResponse;
import xyz.kbws.common.ErrorCode;
import xyz.kbws.common.ResultUtils;
import xyz.kbws.entity.ThreadPoolConfigEntity;
import xyz.kbws.exception.BusinessException;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author kbws
 * @date 2025/4/20
 * @description: 动态线程池接口
 */
@Api(tags = "动态线程池接口")
@Slf4j
@RestController
@RequestMapping("/dynamicThreadPool")
public class DynamicThreadPoolController {

    @Resource
    private RedissonClient redissonClient;

    @ApiOperation(value = "查询线程池数据")
    @GetMapping("/queryThreadPoolList")
    public BaseResponse<List<ThreadPoolConfigEntity>> queryThreadPoolList() {
        try {
            RList<ThreadPoolConfigEntity> cacheList = redissonClient.getList("THREAD_POOL_CONFIG_LIST_KEY");
            return ResultUtils.success(cacheList);
        } catch (Exception e) {
            log.error("查询线程池数据异常", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "查询线程池数据异常");
        }
    }

    @ApiOperation(value = "查询线程池配置")
    @GetMapping("queryThreadPoolConfig")
    public BaseResponse<ThreadPoolConfigEntity> queryThreadPoolConfig(@RequestParam String appName, @RequestParam String threadPoolName) {
        try {
            String cacheKey = "THREAD_POOL_CONFIG_PARAMETER_LIST_KEY" + "_" + appName + "_" + threadPoolName;
            ThreadPoolConfigEntity threadPoolConfigEntity = redissonClient.<ThreadPoolConfigEntity>getBucket(cacheKey).get();
            return ResultUtils.success(threadPoolConfigEntity);
        } catch (Exception e) {
            log.error("查询线程池配置异常", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "查询线程池配置异常");
        }
    }

    @ApiOperation(value = "修改线程池配置")
    @PostMapping("/updateThreadPoolConfig")
    public BaseResponse<Boolean> updateThreadPoolConfig(@RequestBody ThreadPoolConfigEntity entity) {
        try {
            log.info("修改线程池配置开始 {} {} {}", entity.getApplicationName(), entity.getThreadPoolName(), JSONUtil.toJsonStr(entity));
            RTopic topic = redissonClient.getTopic("DYNAMIC_THREAD_POOL_REDIS_TOPIC" + "_" + entity.getApplicationName());
            topic.publish(entity);
            log.info("修改线程池配置完成 {} {}", entity.getApplicationName(), entity.getThreadPoolName());
            return ResultUtils.success(true);
        } catch (Exception e) {
            log.error("修改线程池配置异常", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "修改线程池配置异常");
        }
    }
}
