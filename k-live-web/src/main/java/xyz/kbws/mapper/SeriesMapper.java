package xyz.kbws.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import xyz.kbws.model.entity.Series;

import java.util.List;

/**
 * @author fangyuan
 * @description 针对表【series(视频合集表)】的数据库操作Mapper
 * @createDate 2024-12-09 20:54:21
 * @Entity generator.domain.Series
 */
public interface SeriesMapper extends BaseMapper<Series> {

    List<Series> selectUserAllSeries(@Param("userId") String userId);

    List<Series> selectListWithVideo(@Param("userId") String userId);

    Integer selectMaxSort(@Param("userId") String userId);

    void changeSort(@Param("seriesList") List<Series> seriesList);
}




