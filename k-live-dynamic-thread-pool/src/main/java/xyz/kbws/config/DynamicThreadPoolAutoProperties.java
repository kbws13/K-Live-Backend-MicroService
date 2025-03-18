package xyz.kbws.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author kbws
 * @date 2025/3/17
 * @description: 动态线程池配置
 */
@Data
@ConfigurationProperties(prefix = "dynamic.thread-pool.config", ignoreInvalidFields = true)
public class DynamicThreadPoolAutoProperties {
    /**
     * 状态
     */
    private boolean enable;
    /**
     * Redis Host
     */
    private String host;
    /**
     * Redis Port
     */
    private int port;
    /**
     * Redis Password
     */
    private String password;
    /**
     * 连接池的大小，默认 64
     */
    private int poolSize = 64;
    /**
     * 连接池的最小空闲连接数, 默认 10
     */
    private int minIdleSize = 10;
    /**
     * 连接的最大空闲时间（单位：毫秒），超过该时间的空闲连接将被关闭，默认为 10000
     */
    private int idleTimeout = 10000;
    /**
     * 设置连接超时时间（单位：毫秒）, 默认 10000
     */
    private int connectTimeout = 10000;
    /**
     * 连接重试次数，默认为 3
     */
    private int retryAttempts = 3;
    /**
     * 连接重试的间隔时间（单位：毫秒），默认为 10000
     */
    private int retryInterval = 1000;
    /**
     * 定期检查连接是否可用的时间间隔（单位：毫秒），默认为 0，表示不进行定期检查
     */
    private int pingInterval = 0;
    /**
     * 是否保持长链接，默认为 true
     */
    private boolean keepAlive = true;
}
