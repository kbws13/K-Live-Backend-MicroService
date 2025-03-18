package xyz.kbws.config;

import lombok.extern.slf4j.Slf4j;
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
