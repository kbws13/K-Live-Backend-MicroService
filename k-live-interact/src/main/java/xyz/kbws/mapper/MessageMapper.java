package xyz.kbws.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import xyz.kbws.model.entity.Message;
import xyz.kbws.model.query.MessageQuery;
import xyz.kbws.model.vo.MessageCountVO;

import java.util.List;

/**
 * @author fangyuan
 * @description 针对表【message(用户消息表)】的数据库操作Mapper
 * @createDate 2024-12-15 12:08:31
 * @Entity generator.domain.Message
 */
public interface MessageMapper extends BaseMapper<Message> {

    List<MessageCountVO> getMessageTypeNoReadCount(@Param("userId") String userId);

    List<Message> selectList(@Param("query") MessageQuery query);
}




