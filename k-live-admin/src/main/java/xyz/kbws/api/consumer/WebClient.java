package xyz.kbws.api.consumer;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import xyz.kbws.entity.query.StatisticInfoQuery;
import xyz.kbws.model.entity.StatisticInfo;

import java.util.List;
import java.util.Map;

/**
 * @author kbws
 * @date 2024/12/21
 * @description:
 */
@FeignClient(name = "k-live-web", contextId = "webClient")
public interface WebClient {

    @PostMapping("/inner/statistic/findListTotalInfo")
    List<StatisticInfo> findListTotalInfo(@RequestBody StatisticInfoQuery statisticInfoQuery);

    @PostMapping("/inner/statistic/getTotalStatistic")
    Map<String, Integer> getTotalStatistic(@RequestParam String userId);

    @PostMapping("/inner/statistic/list")
    List<StatisticInfo> list(@RequestBody QueryWrapper<StatisticInfo> queryWrapper);

    @PostMapping("/inner/statistic/findUserCountTotalInfo")
    List<StatisticInfo> findUserCountTotalInfo(@RequestBody StatisticInfoQuery query);

    @PostMapping("/inner/user/count")
    Integer count();
}
