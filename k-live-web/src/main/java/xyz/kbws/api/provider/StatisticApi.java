package xyz.kbws.api.provider;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.web.bind.annotation.*;
import xyz.kbws.model.entity.StatisticInfo;
import xyz.kbws.model.enums.StatisticTypeEnum;
import xyz.kbws.model.query.StatisticInfoQuery;
import xyz.kbws.service.StatisticInfoService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author kbws
 * @date 2024/12/21
 * @description:
 */
@RestController
@RequestMapping("/inner/statistic")
public class StatisticApi {

    @Resource
    private StatisticInfoService statisticInfoService;

    @PostMapping("/findListTotalInfo")
    List<StatisticInfo> findListTotalInfo(StatisticInfoQuery statisticInfoQuery) {
        return statisticInfoService.findListTotalInfo(statisticInfoQuery);
    }

    @PostMapping("/getTotalStatistic")
    Map<String, Integer> getTotalStatistic(String userId) {
        return statisticInfoService.getTotalStatistic(userId);
    }

    @PostMapping("/list")
    List<StatisticInfo> list(@RequestParam Integer dateType) {
        List<String> dateList = getBeforeDays(7);
        List<StatisticInfo> statisticInfoList;
        QueryWrapper<StatisticInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("dataType", dateType)
                .ge("statisticDate", dateList.get(0))
                .le("statisticDate", dateList.get(dateList.size() - 1))
                .orderByAsc("statisticDate");
        statisticInfoList = statisticInfoService.list(queryWrapper);
        return statisticInfoList;
    }

    @PostMapping("/findUserCountTotalInfo")
    List<StatisticInfo> findUserCountTotalInfo(@RequestBody StatisticInfoQuery query) {
        return statisticInfoService.findUserCountTotalInfo(query);
    }

    private List<String> getBeforeDays(Integer beforeDays) {
        DateTime date = DateUtil.date();
        List<String> dateList = new ArrayList<>();
        for (int i = beforeDays; i > 0; i--) {
            String format = DateUtil.format(DateUtil.offsetDay(date, i), "yyyy-MM-dd");
            dateList.add(format);
        }
        return dateList;
    }
}
