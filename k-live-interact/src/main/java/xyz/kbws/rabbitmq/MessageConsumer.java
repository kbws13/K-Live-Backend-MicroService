package xyz.kbws.rabbitmq;

import cn.hutool.core.util.StrUtil;
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
import xyz.kbws.api.consumer.VideoClient;
import xyz.kbws.api.consumer.VideoPlayHistoryClient;
import xyz.kbws.common.ErrorCode;
import xyz.kbws.constant.MqConstant;
import xyz.kbws.exception.BusinessException;
import xyz.kbws.model.dto.video.VideoPlayRequest;
import xyz.kbws.model.enums.SearchOrderTypeEnum;
import xyz.kbws.redis.RedisComponent;

import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author kbws
 * @date 2024/12/22
 * @description: 消息消费者
 */
@Slf4j
@Component
public class MessageConsumer {

    @Resource
    private VideoClient videoClient;

    @Resource
    private VideoPlayHistoryClient videoPlayHistoryClient;

    @Resource
    private RedisComponent redisComponent;

    private ExecutorService executorService = Executors.newFixedThreadPool(4);

    /**
     * 监听并处理视频播放消息
     *
     * @param message
     * @param channel
     * @param deliveryTag
     */
    @SneakyThrows
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = MqConstant.NEWS_QUEUE),
                    exchange = @Exchange(name = MqConstant.NEWS_EXCHANGE_NAME),
                    key = MqConstant.VIDEO_PLAY_ROUTING_KEY
            ),
            ackMode = "MANUAL", concurrency = "2")
    public void receiveNewsMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("receiveNewsMessage message = {}", message);
        if (message == null) {
            // 消息为空，则拒绝消息（不重试），进入死信队列
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(ErrorCode.NULL_ERROR, "消息为空");
        }

        try {
            executorService.execute(() -> {
                VideoPlayRequest videoPlayRequest = JSONUtil.toBean(message, VideoPlayRequest.class);
                videoClient.addPlayCount(videoPlayRequest.getVideoId());
                if (!StrUtil.isEmpty(videoPlayRequest.getUserId())) {
                    // 记录播放历史
                    String videoId = videoPlayRequest.getVideoId();
                    String userId = videoPlayRequest.getUserId();
                    Integer fileIndex = videoPlayRequest.getFileIndex();
                    videoPlayHistoryClient.saveHistory(userId, videoId, fileIndex);
                }
                // 按天记录播放数量
                redisComponent.recordVideoPlayCount(videoPlayRequest.getVideoId());
                // 更新 ES 播放数量
                videoClient.updateDocCount(videoPlayRequest.getVideoId(), SearchOrderTypeEnum.VIDEO_PLAY, 1);

            });
            // 手动确认消息
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            // 抛出异常，进入死信队列
            channel.basicNack(deliveryTag, false, false);
            log.error("处理视频播放消息失败");
        }
    }
}
