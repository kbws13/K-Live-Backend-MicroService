package xyz.kbws.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author kbws
 * @date 2024/12/21
 * @description: 消息生产者
 */
@Component
public class MessageProducer {
    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送消息到指定队列
     *
     * @param queueName 队列名称
     * @param message   消息内容
     */
    public void sendMessage(String queueName, String message) {
        rabbitTemplate.convertAndSend("", queueName, message);
    }
}
