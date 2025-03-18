package xyz.kbws.rabbitmq;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import xyz.kbws.api.consumer.VideoPostClient;
import xyz.kbws.common.ErrorCode;
import xyz.kbws.config.AppConfig;
import xyz.kbws.constant.FileConstant;
import xyz.kbws.constant.MqConstant;
import xyz.kbws.exception.BusinessException;
import xyz.kbws.model.entity.VideoFilePost;
import xyz.kbws.model.enums.VideoFileTransferResultEnum;
import xyz.kbws.model.vo.UploadingFileVO;
import xyz.kbws.redis.RedisComponent;
import xyz.kbws.utils.FFmpegUtil;

import javax.annotation.Resource;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;

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
    private RedisComponent redisComponent;

    @Resource
    private FFmpegUtil fFmpegUtil;

    @Resource
    private AppConfig appConfig;

    @Resource
    private ThreadPoolExecutor transferVideoExecutor;

    /**
     * 监听并处理视频转码消息
     *
     * @param message
     * @param channel
     * @param deliveryTag
     */
    @SneakyThrows
    @RabbitListener(
            queues = {MqConstant.TRANSFER_VIDEO_QUEUE},
            ackMode = "MANUAL", concurrency = "2"
    )
    public void receiveTransferVideoMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("receiveTransferVideoMessage message = {}", message);
        if (message == null) {
            // 消息为空，则拒绝消息（不重试），进入死信队列
            log.warn("收到空消息，拒绝处理，进入死信队列");
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(ErrorCode.NULL_ERROR, "消息为空");
        }

        try {
            transferVideoExecutor.execute(() -> {
                VideoFilePost videoFilePost = JSONUtil.toBean(message, VideoFilePost.class);
                try {
                    UploadingFileVO uploadVideoFile = redisComponent.getUploadVideoFile(videoFilePost.getUserId(), videoFilePost.getUploadId());
                    String tempFilePath = appConfig.getProjectFolder() + FileConstant.FILE_FOLDER + FileConstant.FILE_FOLDER_TEMP + uploadVideoFile.getFilePath();
                    String targetFilePath = appConfig.getProjectFolder() + FileConstant.FILE_FOLDER + FileConstant.FILE_VIDEO + uploadVideoFile.getFilePath();
                    File srcDir = new File(tempFilePath);
                    File destDir = new File(targetFilePath);
                    // 遍历并复制所有文件
                    if (srcDir.isDirectory()) {
                        for (File file : Objects.requireNonNull(srcDir.listFiles())) {
                            FileUtil.copy(file, new File(destDir, file.getName()), false);
                        }
                    }
                    // 删除临时目录
                    FileUtil.del(tempFilePath);
                    // 删除 Redis 记录
                    redisComponent.deleteUploadVideoFile(videoFilePost.getUserId(), videoFilePost.getUploadId());
                    // 合并文件
                    String completeVideo = targetFilePath + FileConstant.TEMP_VIDEO_NAME;
                    this.union(targetFilePath, completeVideo, true);
                    // 获取播放时长
                    Integer duration = fFmpegUtil.getVideoDuration(completeVideo);
                    videoFilePost.setDuration(duration);
                    videoFilePost.setFileSize(new File(completeVideo).length());
                    videoFilePost.setFilePath(FileConstant.FILE_VIDEO + uploadVideoFile.getFilePath());
                    videoFilePost.setTransferResult(VideoFileTransferResultEnum.SUCCESS.getValue());
                    // 将视频转成 TS 分片
                    this.coverVideo2TS(completeVideo);
                } catch (Exception e) {
                    log.error("文件转码失败: {}", e.getMessage());
                    videoFilePost.setTransferResult(VideoFileTransferResultEnum.FAIL.getValue());
                }
                log.info("即将更新 videoFilePost: {}", JSONUtil.toJsonStr(videoFilePost));
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
            queues = {MqConstant.DEL_VIDEO_QUEUE},
            ackMode = "MANUAL", concurrency = "2")
    public void receiveDeleteFileMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("receiveDeleteFileMessage message = {}", message);
        if (message == null) {
            // 消息为空，则拒绝消息（不重试），进入死信队列
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(ErrorCode.NULL_ERROR, "消息为空");
        }

        try {
            transferVideoExecutor.execute(() -> {
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

    private void coverVideo2TS(String completeVideo) {
        File voideFile = new File(completeVideo);
        File tsFolder = voideFile.getParentFile();
        String codec = fFmpegUtil.getVideoCodec(completeVideo);
        if (!FileConstant.VIDEO_CODE_HEVC.equals(codec)) {
            String tempFileName = completeVideo + FileConstant.VIDEO_TEMP_FILE_SUFFIX;
            new File(completeVideo).renameTo(new File(tempFileName));
            fFmpegUtil.coverHevc2Mp4(tempFileName, completeVideo);
            FileUtil.del(tempFileName);
        }
        fFmpegUtil.coverVideo2TS(tsFolder, completeVideo);
        FileUtil.del(voideFile);
    }

    private void union(String dirPath, String toFilePath, Boolean delSource) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "目录不存在");
        }
        File[] fileList = dir.listFiles();
        File targetFile = new File(toFilePath);
        try (RandomAccessFile writeFile = new RandomAccessFile(targetFile, "rw")) {
            byte[] bytes = new byte[1024 * 10];
            for (int i = 0; i < fileList.length; i++) {
                int len;
                // 创建读块文件的对象
                File chunkFile = new File(dirPath + File.separator + i);
                try (RandomAccessFile readFile = new RandomAccessFile(chunkFile, "r")) {
                    while ((len = readFile.read(bytes)) != -1) {
                        writeFile.write(bytes, 0, len);
                    }
                } catch (Exception e) {
                    log.error("合并分片失败: {}", e.getMessage());
                    throw new BusinessException(ErrorCode.OPERATION_ERROR, "合并分片失败" + e.getMessage());
                }
            }
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "合并文件 " + dirPath + " 出错了");
        } finally {
            if (delSource) {
                for (File file : fileList) {
                    file.delete();
                }
            }
        }
    }
}
