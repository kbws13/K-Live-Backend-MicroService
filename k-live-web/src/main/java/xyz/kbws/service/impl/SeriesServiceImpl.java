package xyz.kbws.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.kbws.common.ErrorCode;
import xyz.kbws.exception.BusinessException;
import xyz.kbws.mapper.SeriesContentMapper;
import xyz.kbws.mapper.SeriesMapper;
import xyz.kbws.model.entity.Series;
import xyz.kbws.model.entity.SeriesContent;
import xyz.kbws.model.entity.Video;
import xyz.kbws.service.SeriesContentService;
import xyz.kbws.service.SeriesService;
import xyz.kbws.service.VideoService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author fangyuan
 * @description 针对表【series(视频合集表)】的数据库操作Service实现
 * @createDate 2024-12-09 20:54:21
 */
@Service
public class SeriesServiceImpl extends ServiceImpl<SeriesMapper, Series>
        implements SeriesService {

    @Resource
    private VideoService videoService;

    @Resource
    private SeriesContentService seriesContentService;

    @Resource
    private SeriesMapper seriesMapper;

    @Resource
    private SeriesContentMapper seriesContentMapper;


    @Override
    public List<Series> getUserAllSeries(String userId) {
        return seriesMapper.selectUserAllSeries(userId);
    }

    @Override
    public List<Series> selectListWithVideoList(String userId) {
        return seriesMapper.selectListWithVideo(userId);
    }

    @Override
    public void changeSeriesSort(String userId, String seriesIds) {
        String[] seriesIdArray = seriesIds.split(",");
        List<Series> seriesList = new ArrayList<>();
        int sort = 0;
        for (String seriesId : seriesIdArray) {
            Series series = new Series();
            series.setId(Integer.parseInt(seriesId));
            series.setUserId(userId);
            series.setSort(++sort);
            seriesList.add(series);
        }
        seriesMapper.changeSort(seriesList);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addSeries(Series series, String videoIds) {
        String userId = series.getUserId();
        checkVideoIds(userId, videoIds);
        series.setUpdateTime(DateUtil.date());
        series.setSort(seriesMapper.selectMaxSort(userId) + 1);
        this.save(series);
        saveSeriesContent(userId, series.getId(), videoIds);
    }

    @Override
    public void saveSeriesContent(String userId, Integer seriesId, String videoIds) {
        Series series = this.getById(seriesId);
        if (series == null || !series.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        checkVideoIds(userId, videoIds);
        String[] videoIdArray = videoIds.split(",");
        Integer maxSort = seriesContentMapper.selectMaxSort(seriesId);
        List<SeriesContent> list = new ArrayList<>();
        for (String videoId : videoIdArray) {
            SeriesContent seriesContent = new SeriesContent();
            seriesContent.setSeriesId(seriesId);
            seriesContent.setVideoId(videoId);
            seriesContent.setUserId(userId);
            seriesContent.setSort(++maxSort);
            list.add(seriesContent);
        }
        seriesContentService.saveOrUpdateBatch(list);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteSeries(String userId, Integer seriesId) {
        QueryWrapper<Series> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("seriesId", seriesId)
                .eq("userId", userId);
        boolean remove = this.remove(queryWrapper);
        if (!remove) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        QueryWrapper<SeriesContent> seriesContentQueryWrapper = new QueryWrapper<>();
        seriesContentQueryWrapper.eq("seriesId", seriesId)
                .eq("userId", userId);
        seriesContentService.remove(seriesContentQueryWrapper);
    }

    @Override
    public void deleteSeriesContent(String userId, Integer seriesId, String videoId) {
        QueryWrapper<SeriesContent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId)
                .eq("seriesId", seriesId)
                .eq("videoId", videoId);
        seriesContentService.remove(queryWrapper);
    }

    private void checkVideoIds(String userId, String videoIds) {
        String[] videoIdArray = videoIds.split(",");
        QueryWrapper<Video> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId)
                .in("id", Arrays.asList(videoIdArray));
        long count = videoService.count(queryWrapper);
        if (count != videoIdArray.length) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
    }
}




