package xyz.kbws.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import xyz.kbws.model.entity.Danmu;
import xyz.kbws.model.query.DanmuQuery;

import java.util.List;

/**
 * @author fangyuan
 * @description 针对表【danmu(弹幕表)】的数据库操作Mapper
 * @createDate 2024-12-07 12:13:14
 * @Entity generator.domain.Danmu
 */
public interface DanmuMapper extends BaseMapper<Danmu> {

    List<Danmu> selectList(@Param("query") DanmuQuery query);

    Integer selectCount(@Param("query") DanmuQuery query);
}




