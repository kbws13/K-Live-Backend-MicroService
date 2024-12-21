package xyz.kbws.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.kbws.model.entity.Message;
import xyz.kbws.model.enums.MessageTypeEnum;
import xyz.kbws.model.query.MessageQuery;
import xyz.kbws.model.vo.MessageCountVO;

import java.util.List;

/**
 * @author fangyuan
 * @description 针对表【message(用户消息表)】的数据库操作Service
 * @createDate 2024-12-15 12:08:31
 */
public interface MessageService extends IService<Message> {

    void saveMessage(String videoId, String sendUserId, MessageTypeEnum messageTypeEnum, String content, Integer replyCommentId);

    List<MessageCountVO> getMessageTypeNoReadCount(String userId);

    List<Message> selectList(MessageQuery messageQuery);
}
