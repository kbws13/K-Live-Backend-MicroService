package xyz.kbws.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author kbws
 * @date 2025/3/17
 * @description:
 */
@Data
@ConfigurationProperties(prefix = "thread.pool.executor.config", ignoreInvalidFields = true)
public class ThreadPoolConfigProperties {
    private Integer corePoolSize = 20;

    private Integer maximumPoolSize = 200;

    private Long keepAliveTime = 10L;

    private Integer blockQueueSize = 5000;
    /**
     * AbortPolicy: 丢弃任务并抛出 RejectedExecutionException 异常
     * DiscardPolicy: 直接丢弃任务，但不会抛出异常
     * DiscardOldestPolicy: 将最早进入队列的任务删除，之后再尝试加入队列
     * CallerRunsPolicy: 如果任务添加线程池失败，那么主线程自己执行该任务
     */
    private String policy = "AbortPolicy";
}
