package xyz.kbws.registry;

import xyz.kbws.domain.model.entity.ThreadPoolConfigEntity;

import java.util.List;

/**
 * @author kbws
 * @date 2025/3/17
 * @description: 注册中心接口
 */
public interface IRegistry {

    void registryThreadPool(List<ThreadPoolConfigEntity> threadPoolConfigEntities);

    void reportThreadPoolConfigParameter(ThreadPoolConfigEntity threadPoolConfigEntity);
}
