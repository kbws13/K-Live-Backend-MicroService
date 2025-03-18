package xyz.kbws.tigger.listener;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.listener.MessageListener;
import xyz.kbws.domain.IDynamicThreadPoolService;
import xyz.kbws.domain.model.entity.ThreadPoolConfigEntity;
import xyz.kbws.registry.IRegistry;

/**
 * @author kbws
 * @date 2025/3/17
 * @description: 动态线程池变更监听
 */
@Slf4j
public class ThreadPoolConfigAdjustListener implements MessageListener<ThreadPoolConfigEntity> {

    private final IDynamicThreadPoolService dynamicThreadPoolService;

    private final IRegistry registry;

    public ThreadPoolConfigAdjustListener(IDynamicThreadPoolService dynamicThreadPoolService, IRegistry registry) {
        this.dynamicThreadPoolService = dynamicThreadPoolService;
        this.registry = registry;
    }

    @Override
    public void onMessage(CharSequence charSequence, ThreadPoolConfigEntity threadPoolConfigEntity) {
        log.info("监听到配置变化 调整线程池配置 线程池名称: {} 核心线程数: {} 最大线程数: {}", threadPoolConfigEntity.getThreadPoolName(), threadPoolConfigEntity.getCorePoolSize(), threadPoolConfigEntity.getMaximumPoolSize());

        dynamicThreadPoolService.updateThreadPoolConfig(threadPoolConfigEntity);
        // 更新后上报最新数据
        ThreadPoolConfigEntity current = dynamicThreadPoolService.getThreadPoolConfigByName(threadPoolConfigEntity.getThreadPoolName());
        registry.reportThreadPoolConfigParameter(current);
        log.info("监听配置变化 上报线程池配置: {}", JSONUtil.toJsonStr(current));
    }
}
