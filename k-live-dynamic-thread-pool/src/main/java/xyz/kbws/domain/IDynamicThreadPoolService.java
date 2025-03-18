package xyz.kbws.domain;

import xyz.kbws.domain.model.entity.ThreadPoolConfigEntity;

import java.util.List;

/**
 * @author kbws
 * @date 2025/3/17
 * @description: 动态线程池服务
 */
public interface IDynamicThreadPoolService {

    List<ThreadPoolConfigEntity> getThreadPoolList();

    ThreadPoolConfigEntity getThreadPoolConfigByName(String threadPoolName);

    void updateThreadPoolConfig(ThreadPoolConfigEntity threadPoolConfigEntity);
}
