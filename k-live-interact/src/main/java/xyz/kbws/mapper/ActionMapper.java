package xyz.kbws.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import xyz.kbws.model.entity.Action;
import xyz.kbws.model.query.ActionQuery;

import java.util.List;

/**
 * @author fangyuan
 * @description 针对表【action(用户行为 点赞、评论)】的数据库操作Mapper
 * @createDate 2024-12-07 12:13:10
 * @Entity generator.domain.Action
 */
public interface ActionMapper extends BaseMapper<Action> {

    List<Action> findList(@Param("query") ActionQuery query);
}




