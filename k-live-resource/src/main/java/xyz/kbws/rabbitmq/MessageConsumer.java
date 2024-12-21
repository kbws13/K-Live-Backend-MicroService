package xyz.kbws.rabbitmq;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import xyz.kbws.api.consumer.VideoPostClient;
import xyz.kbws.common.ErrorCode;
import xyz.kbws.config.AppConfig;
import xyz.kbws.constant.MqConstant;
import xyz.kbws.exception.BusinessException;
import xyz.kbws.model.entity.VideoFilePost;

import javax.annotation.Resource;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author kbws
 * @date 2024/12/21
 * @description: 消息消费者
 */
@Slf4j
@Component
public class MessageConsumer {

    @Resource
    private VideoPostClient videoPostClient;

    @Resource
    private AppConfig appConfig;

    private ExecutorService executorService = Executors.newFixedThreadPool(4);

    /**
     * 监听并处理视频转码消息
     *
     * @param message
     * @param channel
     * @param deliveryTag
     */
    @SneakyThrows
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = MqConstant.FILE_QUEUE),
                    exchange = @Exchange(name = MqConstant.FILE_EXCHANGE_NAME),
                    key = MqConstant.TRANSFER_VIDEO_ROOTING_KEY
            ),
            ackMode = "MANUAL", concurrency = "2")
    public void receiveTransferVideoMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("receiveTransferVideoMessage message = {}", message);
        if (message == null) {
            // 消息为空，则拒绝消息（不重试），进入死信队列
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(ErrorCode.NULL_ERROR, "消息为空");
        }

        try {
            executorService.execute(() -> {
                VideoFilePost videoFilePost = JSONUtil.toBean(message, VideoFilePost.class);
                videoPostClient.transferVideoFile(videoFilePost);
            });
            // 手动确认消息
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            // 抛出异常，进入死信队列
            channel.basicNack(deliveryTag, false, false);
        }
    }

    /**
     * 监听并处理删除视频文件消息
     *
     * @param message
     * @param channel
     * @param deliveryTag
     */
    @SneakyThrows
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = MqConstant.FILE_QUEUE),
                    exchange = @Exchange(name = MqConstant.FILE_EXCHANGE_NAME),
                    key = MqConstant.DEL_FILE_ROUTING_KEY
            ),
            ackMode = "MANUAL", concurrency = "2")
    public void receiveDeleteFileMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("receiveDeleteFileMessage message = {}", message);
        if (message == null) {
            // 消息为空，则拒绝消息（不重试），进入死信队列
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(ErrorCode.NULL_ERROR, "消息为空");
        }

        try {
            executorService.execute(() -> {
                VideoFilePost videoFilePost = JSONUtil.toBean(message, VideoFilePost.class);
                boolean del = FileUtil.del(new File(appConfig.getProjectFolder() + videoFilePost.getFilePath()));
                if (!del) {
                    log.error("删除文件失败, 文件路径: {}", videoFilePost.getFilePath());
                }
            });
            // 手动确认消息
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            // 抛出异常，进入死信队列
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
