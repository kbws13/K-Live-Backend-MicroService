package xyz.kbws.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import xyz.kbws.constant.MqConstant;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * @author kbws
 * @date 2024/12/21
 * @description: 用于创建程序用到的交换机和队列（只用在程序启动前执行一次）
 */
@Slf4j
@Component
public class InitRabbitMqBean {
    @Value("${spring.rabbitmq.host:localhost}")
    private String host;

    @PostConstruct
    public void init() {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(host);
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            // 创建 news 交换机
            channel.exchangeDeclare(MqConstant.NEWS_EXCHANGE_NAME, MqConstant.NEWS_DIRECT_EXCHANGE, true);
            channel.queueDeclare(MqConstant.NEWS_QUEUE, true, false, false, null);
            channel.queueBind(MqConstant.NEWS_QUEUE, MqConstant.NEWS_EXCHANGE_NAME, MqConstant.VIDEO_PLAY_ROUTING_KEY);

            log.info("消息队列启动成功");
        } catch (Exception e) {
            log.error("消息队列启动失败: {}", e.getMessage());
        }
    }
}
