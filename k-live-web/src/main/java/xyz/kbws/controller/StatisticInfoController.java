package xyz.kbws.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.kbws.annotation.AuthCheck;
import xyz.kbws.common.BaseResponse;
import xyz.kbws.common.ResultUtils;
import xyz.kbws.model.entity.StatisticInfo;
import xyz.kbws.model.vo.UserVO;
import xyz.kbws.redis.RedisComponent;
import xyz.kbws.service.StatisticInfoService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author kbws
 * @date 2024/12/21
 * @description: 统计信息接口
 */
@Api(tags = "统计信息接口")
@RestController
@RequestMapping("/statistic")
public class StatisticInfoController {

    @Resource
    private StatisticInfoService statisticInfoService;

    @Resource
    private RedisComponent redisComponent;

    @ApiOperation(value = "获取昨天统计数据")
    @AuthCheck
    @GetMapping("/getActualTime")
    public BaseResponse<Map<String, Object>> getActualTime(HttpServletRequest request) {
        String token = request.getHeader("token");
        UserVO userVO = redisComponent.getUserVO(token);

        String yesterday = DateUtil.format(DateUtil.yesterday(), "yyyy-MM-dd");
        QueryWrapper<StatisticInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("statisticDate", yesterday)
                .eq("userId", userVO.getId());
        List<StatisticInfo> statisticInfoList = statisticInfoService.list(queryWrapper);
        Map<Integer, Integer> preDayInfo = statisticInfoList.stream()
                .collect(
                        Collectors.toMap(
                                StatisticInfo::getDataType, StatisticInfo::getCount, (item1, item2) -> item2
                        )
                );
        Map<String, Integer> totalCountInfo = statisticInfoService.getTotalStatistic(userVO.getId());
        Map<String, Object> res = new HashMap<>();
        res.put("preDayInfo", preDayInfo);
        res.put("totalCountInfo", totalCountInfo);
        return ResultUtils.success(res);
    }

    @ApiOperation(value = "获取本周数据")
    @AuthCheck
    @GetMapping("/getWeek")
    public BaseResponse<List<StatisticInfo>> getWeek(Integer dateType, HttpServletRequest request) {
        String token = request.getHeader("token");
        UserVO userVO = redisComponent.getUserVO(token);
        List<String> beforeDays = getBeforeDays(7);
        String startTime = beforeDays.get(0);
        String endTime = beforeDays.get(beforeDays.size() - 1);
        QueryWrapper<StatisticInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("dateType", dateType)
                .eq("userId", userVO.getId())
                .ge("statisticDate", startTime)
                .le("statisticDate", endTime)
                .orderByAsc("statisticDate");
        List<StatisticInfo> statisticInfoList = statisticInfoService.list(queryWrapper);
        Map<String, StatisticInfo> dataMap = statisticInfoList.stream()
                .collect(Collectors.toMap(StatisticInfo::getStatisticDate, Function.identity(), (item1, item2) -> item2));
        List<StatisticInfo> res = new ArrayList<>();
        for (String beforeDay : beforeDays) {
            StatisticInfo item = dataMap.get(beforeDay);
            if (item == null) {
                item = new StatisticInfo();
                item.setCount(0);
                item.setStatisticDate(beforeDay);
            }
            res.add(item);
        }
        return ResultUtils.success(res);
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
