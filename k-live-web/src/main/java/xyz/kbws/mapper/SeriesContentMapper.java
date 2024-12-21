package xyz.kbws.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import xyz.kbws.model.entity.SeriesContent;
import xyz.kbws.model.query.SeriesContentQuery;

import java.util.List;

/**
 * @author fangyuan
 * @description 针对表【seriesContent(视频合集内容表)】的数据库操作Mapper
 * @createDate 2024-12-09 20:54:24
 * @Entity generator.domain.Seriescontent
 */
public interface SeriesContentMapper extends BaseMapper<SeriesContent> {

    Integer selectMaxSort(@Param("seriesId") Integer seriesId);

    List<SeriesContent> selectList(@Param("query") SeriesContentQuery query);
}




