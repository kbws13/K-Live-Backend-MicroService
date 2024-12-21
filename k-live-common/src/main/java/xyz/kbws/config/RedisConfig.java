package xyz.kbws.config;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * @author kbws
 * @date 2024/11/24
 * @description:
 */
@Slf4j
@Configuration
public class RedisConfig<V> {

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

    //@Bean
    //public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory) {
    //    RedisMessageListenerContainer container = new RedisMessageListenerContainer();
    //    container.setConnectionFactory(connectionFactory);
    //    return container;
    //}

    @Bean
    public RedisTemplate<String, V> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, V> template = new RedisTemplate<String, V>();
        template.setConnectionFactory(connectionFactory);
        // 设置 Key 的序列化方式
        template.setKeySerializer(RedisSerializer.string());
        // 设置 Value 的序列化方式
        template.setValueSerializer(RedisSerializer.json());
        // 设置 Hash 的 Key 序列化方式
        template.setHashKeySerializer(RedisSerializer.string());
        // 设置 Hash 的 Value 序列化方式
        template.setHashValueSerializer(RedisSerializer.json());
        return template;
    }
}
