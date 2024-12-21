package xyz.kbws.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import xyz.kbws.model.entity.StatisticInfo;
import xyz.kbws.model.query.StatisticInfoQuery;

import java.util.List;
import java.util.Map;

/**
 * @author fangyuan
 * @description 针对表【statisticInfo(数据统计表)】的数据库操作Mapper
 * @createDate 2024-12-15 12:09:09
 * @Entity generator.domain.StatisticInfo
 */
public interface StatisticInfoMapper extends BaseMapper<StatisticInfo> {

    List<StatisticInfo> selectFans(@Param("statisticDate") String statisticDate);

    List<StatisticInfo> selectComment(@Param("statisticDate") String statisticDate);

    List<StatisticInfo> selectAction(@Param("statisticDate") String statisticDate
            , @Param("actionTypeArray") Integer[] actionTypeArray);

    Map<String, Integer> selectTotalCount(@Param("userId") String userId);

    List<StatisticInfo> selectListTotalInfo(@Param("query") StatisticInfoQuery query);

    List<StatisticInfo> selectUserCountTotalInfo(@Param("query") StatisticInfoQuery query);
}




