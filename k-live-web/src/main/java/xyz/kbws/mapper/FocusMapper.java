package xyz.kbws.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import xyz.kbws.model.entity.Focus;
import xyz.kbws.model.query.FocusQuery;
import xyz.kbws.model.vo.FocusVO;

import java.util.List;

/**
 * @author fangyuan
 * @description 针对表【focus(关注表)】的数据库操作Mapper
 * @createDate 2024-12-09 20:54:18
 * @Entity generator.domain.Focus
 */
public interface FocusMapper extends BaseMapper<Focus> {

    Integer selectFansCount(@Param("userId") String userId);

    Integer selectFocusCount(@Param("userId") String userId);

    List<FocusVO> selectList(@Param("query") FocusQuery query);
}




