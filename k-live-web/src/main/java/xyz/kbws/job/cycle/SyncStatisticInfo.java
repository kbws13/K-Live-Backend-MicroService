package xyz.kbws.job.cycle;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import xyz.kbws.service.StatisticInfoService;

import javax.annotation.Resource;

/**
 * @author kbws
 * @date 2024/12/22
 * @description: 定时同步统计数据到数据库
 */
@Component
public class SyncStatisticInfo {

    @Resource
    private StatisticInfoService statisticInfoService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void syncStatisticInfo() {
        statisticInfoService.syncStatisticInfoData();
    }
}
