package xyz.kbws.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.kbws.model.entity.StatisticInfo;
import xyz.kbws.model.query.StatisticInfoQuery;

import java.util.List;
import java.util.Map;

/**
 * @author fangyuan
 * @description 针对表【statisticInfo(数据统计表)】的数据库操作Service
 * @createDate 2024-12-15 12:09:09
 */
public interface StatisticInfoService extends IService<StatisticInfo> {

    void syncStatisticInfoData();

    Map<String, Integer> getTotalStatistic(String userId);

    List<StatisticInfo> findListTotalInfo(StatisticInfoQuery query);

    List<StatisticInfo> findUserCountTotalInfo(StatisticInfoQuery query);
}
