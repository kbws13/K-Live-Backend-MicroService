package xyz.kbws.api.provider;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.kbws.model.entity.StatisticInfo;
import xyz.kbws.model.query.StatisticInfoQuery;
import xyz.kbws.service.StatisticInfoService;

import javax.annotation.Resource;
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
    List<StatisticInfo> list(@RequestBody QueryWrapper<StatisticInfo> queryWrapper) {
        return statisticInfoService.list(queryWrapper);
    }

    @PostMapping("/findUserCountTotalInfo")
    List<StatisticInfo> findUserCountTotalInfo(@RequestBody StatisticInfoQuery query) {
        return statisticInfoService.findUserCountTotalInfo(query);
    }
}
