package xyz.kbws.domain;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import xyz.kbws.domain.model.entity.ThreadPoolConfigEntity;

import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author kbws
 * @date 2025/3/17
 * @description: 动态线程池服务
 */
@Slf4j
public class DynamicThreadPoolService implements IDynamicThreadPoolService {

    private final Map<String, ThreadPoolExecutor> threadPoolExecutorMap;

    private final String applicationName;

    public DynamicThreadPoolService(String applicationName, Map<String, ThreadPoolExecutor> threadPoolExecutorMap) {
        this.applicationName = applicationName;
        this.threadPoolExecutorMap = threadPoolExecutorMap;
    }

    @Override
    public List<ThreadPoolConfigEntity> getThreadPoolList() {
        Set<String> threadPoolBeanNames = threadPoolExecutorMap.keySet();
        List<ThreadPoolConfigEntity> threadPoolVOS = new ArrayList<>(threadPoolBeanNames.size());
        for (String threadPoolBeanName : threadPoolBeanNames) {
            ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(threadPoolBeanName);
            ThreadPoolConfigEntity config = new ThreadPoolConfigEntity(applicationName, threadPoolBeanName);
            config.setCorePoolSize(threadPoolExecutor.getCorePoolSize());
            config.setMaximumPoolSize(threadPoolExecutor.getMaximumPoolSize());
            config.setActiveCount(threadPoolExecutor.getActiveCount());
            config.setPoolSize(threadPoolExecutor.getPoolSize());
            config.setQueueType(threadPoolExecutor.getQueue().getClass().getSimpleName());
            config.setQueueSize(threadPoolExecutor.getQueue().size());
            config.setRemainCapacity(threadPoolExecutor.getQueue().remainingCapacity());
            threadPoolVOS.add(config);

        }
        return threadPoolVOS;
    }

    @Override
    public ThreadPoolConfigEntity getThreadPoolConfigByName(String threadPoolName) {
        ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(threadPoolName);
        if (threadPoolExecutor == null) {
            return new ThreadPoolConfigEntity(applicationName, threadPoolName);
        }
        ThreadPoolConfigEntity config = new ThreadPoolConfigEntity(applicationName, threadPoolName);
        config.setCorePoolSize(threadPoolExecutor.getCorePoolSize());
        config.setMaximumPoolSize(threadPoolExecutor.getMaximumPoolSize());
        config.setActiveCount(threadPoolExecutor.getActiveCount());
        config.setPoolSize(threadPoolExecutor.getPoolSize());
        config.setQueueType(threadPoolExecutor.getQueue().getClass().getSimpleName());
        config.setQueueSize(threadPoolExecutor.getQueue().size());
        config.setRemainCapacity(threadPoolExecutor.getQueue().remainingCapacity());
        if (log.isDebugEnabled()) {
            log.info("动态线程池，配置查询 应用名: {}, 线程池名: {}, 池化配置: {}", applicationName, threadPoolName, JSONUtil.toJsonStr(config));
        }
        return config;
    }

    @Override
    public void updateThreadPoolConfig(ThreadPoolConfigEntity threadPoolConfigEntity) {
        if (threadPoolConfigEntity == null || !applicationName.equals(threadPoolConfigEntity.getApplicationName())) {
            return;
        }
        ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(threadPoolConfigEntity.getThreadPoolName());
        if (threadPoolExecutor == null) {
            return;
        }
        // 设置参数 调整核心线程数和最大线程数
        threadPoolExecutor.setCorePoolSize(threadPoolExecutor.getCorePoolSize());
        threadPoolExecutor.setMaximumPoolSize(threadPoolExecutor.getMaximumPoolSize());
    }
}
