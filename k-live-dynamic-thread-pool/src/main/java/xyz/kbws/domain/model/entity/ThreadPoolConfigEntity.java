package xyz.kbws.domain.model.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author kbws
 * @date 2025/3/17
 * @description: 线程池配置对象
 */
@Data
public class ThreadPoolConfigEntity implements Serializable {
    /**
     * 应用名称
     */
    private String applicationName;

    /**
     * 线程池名称
     */
    private String threadPoolName;

    /**
     * 核心线程数
     */
    private int corePoolSize;

    /**
     * 最大线程数
     */
    private int maximumPoolSize;

    /**
     * 当前活跃线程数
     */
    private int activeCount;

    /**
     * 当前池中线程数
     */
    private int poolSize;

    /**
     * 队列类型
     */
    private String queueType;

    /**
     * 当前队列任务数
     */
    private int queueSize;

    /**
     * 队列剩余任务数
     */
    private int remainCapacity;

    private static final long serialVersionUID = 2955663421200539398L;

    public ThreadPoolConfigEntity() {
    }

    public ThreadPoolConfigEntity(String applicationName, String threadPoolName) {
        this.applicationName = applicationName;
        this.threadPoolName = threadPoolName;
    }
}
