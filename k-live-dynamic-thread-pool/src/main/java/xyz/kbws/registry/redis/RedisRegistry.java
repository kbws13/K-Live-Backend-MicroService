package xyz.kbws.registry.redis;

import org.redisson.api.RBucket;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import xyz.kbws.domain.model.entity.ThreadPoolConfigEntity;
import xyz.kbws.domain.model.valobj.RegistryEnum;
import xyz.kbws.registry.IRegistry;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author kbws
 * @date 2025/3/17
 * @description: Redis 作为注册中心
 */
public class RedisRegistry implements IRegistry {

    private final RedissonClient redissonClient;

    public RedisRegistry(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public void registryThreadPool(List<ThreadPoolConfigEntity> threadPoolConfigEntities) {
        RList<ThreadPoolConfigEntity> list = redissonClient.getList(RegistryEnum.THREAD_POOL_CONFIG_LIST_KEY.getKey());
        list.delete();
        list.addAll(threadPoolConfigEntities);
    }

    @Override
    public void reportThreadPoolConfigParameter(ThreadPoolConfigEntity threadPoolConfigEntity) {
        String cacheKey = RegistryEnum.THREAD_POOL_CONFIG_LIST_KEY.getKey() + "_" + threadPoolConfigEntity.getApplicationName() + "_" + threadPoolConfigEntity.getThreadPoolName();
        RBucket<ThreadPoolConfigEntity> bucket = redissonClient.getBucket(cacheKey);
        bucket.set(threadPoolConfigEntity, 30, TimeUnit.DAYS);
    }
}
