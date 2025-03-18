package xyz.kbws.domain.model.valobj;

import lombok.Getter;

/**
 * @author kbws
 * @date 2025/3/17
 * @description: 注册中心枚举
 */
@Getter
public enum RegistryEnum {

    THREAD_POOL_CONFIG_LIST_KEY("THREAD_POOL_CONFIG_LIST_KEY", "池化配置列表"),
    THREAD_POOL_CONFIG_PARAMETER_LIST_KEY("THREAD_POOL_CONFIG_PARAMETER_LIST_KEY", "池化配置参数"),
    DYNAMIC_THREAD_POOL_REDIS_TOPIC("DYNAMIC_THREAD_POOL_REDIS_TOPIC", "动态线程池监听主题配置");

    private final String key;
    private final String desc;

    RegistryEnum(String key, String desc) {
        this.key = key;
        this.desc = desc;
    }
}
