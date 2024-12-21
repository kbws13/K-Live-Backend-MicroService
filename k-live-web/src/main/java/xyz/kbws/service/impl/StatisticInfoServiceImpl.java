package xyz.kbws.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.kbws.constant.RedisConstant;
import xyz.kbws.mapper.FocusMapper;
import xyz.kbws.mapper.StatisticInfoMapper;
import xyz.kbws.mapper.UserMapper;
import xyz.kbws.mapper.VideoMapper;
import xyz.kbws.model.entity.StatisticInfo;
import xyz.kbws.model.entity.Video;
import xyz.kbws.model.enums.StatisticTypeEnum;
import xyz.kbws.model.enums.UserActionTypeEnum;
import xyz.kbws.model.query.StatisticInfoQuery;
import xyz.kbws.redis.RedisComponent;
import xyz.kbws.service.StatisticInfoService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author fangyuan
 * @description 针对表【statisticInfo(数据统计表)】的数据库操作Service实现
 * @createDate 2024-12-15 12:09:09
 */
@Service
public class StatisticInfoServiceImpl extends ServiceImpl<StatisticInfoMapper, StatisticInfo>
        implements StatisticInfoService {

    @Resource
    private StatisticInfoMapper statisticInfoMapper;

    @Resource
    private VideoMapper videoMapper;

    @Resource
    private FocusMapper focusMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisComponent redisComponent;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void syncStatisticInfoData() {
        List<StatisticInfo> statisticInfoList = new ArrayList<>();
        String yesterday = DateUtil.format(DateUtil.yesterday(), "yyyy-MM-dd");
        // 统计播放量
        Map<String, Integer> videoPlayCountMap = redisComponent.getVideoPlayCount(yesterday);
        List<String> videoPlayKeys = new ArrayList<>(videoPlayCountMap.keySet());
        videoPlayKeys = videoPlayKeys.stream()
                .map(item -> item.substring(item.lastIndexOf(":") + 1))
                .collect(Collectors.toList());
        QueryWrapper<Video> videoQueryWrapper = new QueryWrapper<>();
        videoQueryWrapper.in("id", videoPlayKeys);
        List<Video> videoList = videoMapper.selectList(videoQueryWrapper);

        Map<String, Integer> videoCountMap = videoList.stream()
                .collect(Collectors.groupingBy(
                        Video::getUserId,
                        Collectors.summingInt(
                                item -> videoPlayCountMap.get(
                                        RedisConstant.VIDEO_PLAY_COUNT + yesterday + ":" + item.getId()
                                )
                        )
                ));
        videoCountMap.forEach((k, v) -> {
            StatisticInfo statisticInfo = new StatisticInfo();
            statisticInfo.setStatisticDate(yesterday);
            statisticInfo.setUserId(k);
            statisticInfo.setDataType(StatisticTypeEnum.PLAY.getValue());
            statisticInfo.setCount(v);
            statisticInfoList.add(statisticInfo);
        });

        // 统计粉丝数
        List<StatisticInfo> fansDataList = statisticInfoMapper.selectFans(yesterday);
        for (StatisticInfo statisticInfo : fansDataList) {
            statisticInfo.setStatisticDate(yesterday);
            statisticInfo.setDataType(StatisticTypeEnum.FANS.getValue());
        }
        statisticInfoList.addAll(fansDataList);

        // 统计评论数
        List<StatisticInfo> commentDataList = statisticInfoMapper.selectComment(yesterday);
        for (StatisticInfo statisticInfo : commentDataList) {
            statisticInfo.setStatisticDate(yesterday);
            statisticInfo.setDataType(StatisticTypeEnum.COMMENT.getValue());
        }
        statisticInfoList.addAll(commentDataList);

        // 弹幕、点赞、收藏、投币
        Integer[] actionTypeArray = new Integer[]{
                UserActionTypeEnum.VIDEO_LIKE.getValue(),
                UserActionTypeEnum.VIDEO_COIN.getValue(),
                UserActionTypeEnum.VIDEO_DANMU.getValue(),
                UserActionTypeEnum.VIDEO_COLLECT.getValue()
        };
        List<StatisticInfo> actionDataList = statisticInfoMapper.selectAction(yesterday, actionTypeArray);
        for (StatisticInfo statisticInfo : actionDataList) {
            statisticInfo.setStatisticDate(yesterday);
        }
        statisticInfoList.addAll(actionDataList);

        this.saveOrUpdateBatch(statisticInfoList);
    }

    @Override
    public Map<String, Integer> getTotalStatistic(String userId) {
        Map<String, Integer> res = statisticInfoMapper.selectTotalCount(userId);
        if (!StrUtil.isEmpty(userId)) {
            res.put("fansCount", focusMapper.selectFansCount(userId));
        } else {
            res.put("userCount", Math.toIntExact(userMapper.selectCount(new QueryWrapper<>())));
        }
        return res;
    }

    @Override
    public List<StatisticInfo> findListTotalInfo(StatisticInfoQuery query) {
        return statisticInfoMapper.selectListTotalInfo(query);
    }

    @Override
    public List<StatisticInfo> findUserCountTotalInfo(StatisticInfoQuery query) {
        return statisticInfoMapper.selectUserCountTotalInfo(query);
    }
}




