package xyz.kbws.config;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author kbws
 * @date 2025/3/18
 * @description:
 */
@Slf4j
@Configuration
public class RedisConfig {
    @Value("${spring.redis.host:}")
    private String redisHost;

    @Value("${spring.redis.port:}")
    private Integer redisPort;

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient() {
        try {
            Config config = new Config();
            config.useSingleServer().setAddress("redis://" + redisHost + ":" + redisPort);
            return Redisson.create(config);
        } catch (Exception e) {
            log.info("Redis 配置错误，请检查 Redis 配置");
        }
        return null;
    }
}
