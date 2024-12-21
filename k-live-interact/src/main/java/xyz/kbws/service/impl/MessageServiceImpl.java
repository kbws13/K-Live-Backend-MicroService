package xyz.kbws.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import xyz.kbws.api.consumer.VideoClient;
import xyz.kbws.api.consumer.VideoPostClient;
import xyz.kbws.mapper.MessageMapper;
import xyz.kbws.mapper.VideoCommentMapper;
import xyz.kbws.model.dto.message.MessageExtendDTO;
import xyz.kbws.model.entity.Message;
import xyz.kbws.model.entity.Video;
import xyz.kbws.model.entity.VideoComment;
import xyz.kbws.model.entity.VideoPost;
import xyz.kbws.model.enums.MessageReadTypeEnum;
import xyz.kbws.model.enums.MessageTypeEnum;
import xyz.kbws.model.query.MessageQuery;
import xyz.kbws.model.vo.MessageCountVO;
import xyz.kbws.service.MessageService;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author fangyuan
 * @description 针对表【message(用户消息表)】的数据库操作Service实现
 * @createDate 2024-12-15 12:08:31
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message>
        implements MessageService {

    @Resource
    private MessageMapper messageMapper;

    @Resource
    private VideoClient videoClient;

    @Resource
    private VideoCommentMapper videoCommentMapper;

    @Resource
    private VideoPostClient videoPostClient;

    @Async
    @Override
    public void saveMessage(String videoId, String sendUserId, MessageTypeEnum messageTypeEnum, String content, Integer replyCommentId) {
        Video video = videoClient.selectById(videoId);
        if (video == null) {
            return;
        }
        MessageExtendDTO messageExtendDTO = new MessageExtendDTO();
        messageExtendDTO.setMessageContent(content);
        String receiveUserId = video.getUserId();
        // 点赞、收藏，已经记录过就不再记录
        if (ArrayUtil.contains(new Integer[]{MessageTypeEnum.LIKE.getValue(), MessageTypeEnum.COLLECTION.getValue()}, messageTypeEnum.getValue())) {
            QueryWrapper<Message> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userId", receiveUserId)
                    .eq("videoId", videoId)
                    .eq("type", messageTypeEnum.getValue());
            long count = this.count(queryWrapper);
            if (count > 0) {
                return;
            }
        }
        Message message = new Message();
        message.setVideoId(videoId);
        message.setSendUserId(sendUserId);
        message.setReadType(MessageReadTypeEnum.NO_READ.getValue());
        message.setCreateTime(DateUtil.date());
        message.setType(messageTypeEnum.getValue());
        // 评论特殊处理
        if (replyCommentId != null) {
            VideoComment videoComment = videoCommentMapper.selectById(replyCommentId);
            if (videoComment != null) {
                receiveUserId = videoComment.getUserId();
                messageExtendDTO.setMessageContentReply(videoComment.getContent());
            }
        }
        if (receiveUserId.equals(sendUserId)) {
            return;
        }
        // 系统消息特殊处理
        if (messageTypeEnum == MessageTypeEnum.SYSTEM) {
            VideoPost videoPost = videoPostClient.selectById(videoId);
            messageExtendDTO.setAuditStatus(videoPost.getStatus());
        }
        message.setUserId(receiveUserId);
        message.setExtendJson(JSONUtil.toJsonStr(messageExtendDTO));
        this.save(message);
    }

    @Override
    public List<MessageCountVO> getMessageTypeNoReadCount(String userId) {
        return messageMapper.getMessageTypeNoReadCount(userId);
    }

    @Override
    public List<Message> selectList(MessageQuery messageQuery) {
        return messageMapper.selectList(messageQuery);
    }
}




