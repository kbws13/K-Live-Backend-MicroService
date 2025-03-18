package xyz.kbws.tigger.job;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import xyz.kbws.domain.IDynamicThreadPoolService;
import xyz.kbws.domain.model.entity.ThreadPoolConfigEntity;
import xyz.kbws.registry.IRegistry;

import java.util.List;

/**
 * @author kbws
 * @date 2025/3/17
 * @description: 线程池数据上报任务
 */
@Slf4j
public class ThreadPoolDataReportJob {

    private final IDynamicThreadPoolService dynamicThreadPoolService;

    private final IRegistry registry;

    public ThreadPoolDataReportJob(IDynamicThreadPoolService dynamicThreadPoolService, IRegistry registry) {
        this.dynamicThreadPoolService = dynamicThreadPoolService;
        this.registry = registry;
    }

    @Scheduled(cron = "0/20 * * * * ?")
    public void execReportThreadPoolList() {
        List<ThreadPoolConfigEntity> threadPoolConfigEntities = dynamicThreadPoolService.getThreadPoolList();
        registry.registryThreadPool(threadPoolConfigEntities);
        log.info("自动上报任务 上报线程池信息: {}", JSONUtil.toJsonStr(threadPoolConfigEntities));

        for (ThreadPoolConfigEntity entity : threadPoolConfigEntities) {
            registry.reportThreadPoolConfigParameter(entity);
            log.info("自动上报任务 上报线程池配置: {}", JSONUtil.toJsonStr(entity));
        }
    }
}
