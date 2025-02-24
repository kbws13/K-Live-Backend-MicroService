package xyz.kbws.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import xyz.kbws.constant.MqConstant;

import javax.annotation.PostConstruct;

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

    @Value("${spring.rabbitmq.port:5672}")
    private Integer port;

    @Value("${spring.rabbitmq.username:guest}")
    private String username;

    @Value("${spring.rabbitmq.password:guest}")
    private String password;

    @PostConstruct
    public void init() {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(host);
            factory.setPort(port);
            factory.setUsername(username);
            factory.setPassword(password);
            try (Connection connection = factory.newConnection();
                 Channel channel = connection.createChannel()) {
                // 创建 transfer_video 队列
                channel.queueDeclare(MqConstant.TRANSFER_VIDEO_QUEUE, true, false, false, null);
                channel.queueDeclare(MqConstant.DEL_VIDEO_QUEUE, true, false, false, null);
                log.info("转码视频队列 {} 创建成功", MqConstant.TRANSFER_VIDEO_QUEUE);
                log.info("删除视频文件队列 {} 创建成功", MqConstant. DEL_VIDEO_QUEUE);
            }
        } catch (Exception e) {
            log.error("转码视频队列 {} 创建失败，消息队列启动失败: {}", MqConstant.TRANSFER_VIDEO_QUEUE, e.getMessage());
        }
    }
}
