package xyz.kbws.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.kbws.model.entity.Series;

import java.util.List;

/**
 * @author fangyuan
 * @description 针对表【series(视频合集表)】的数据库操作Service
 * @createDate 2024-12-09 20:54:21
 */
public interface SeriesService extends IService<Series> {

    List<Series> getUserAllSeries(String userId);

    List<Series> selectListWithVideoList(String userId);

    void addSeries(Series series, String videoIds);

    void changeSeriesSort(String userId, String seriesIds);

    void deleteSeries(String userId, Integer seriesId);

    void saveSeriesContent(String userId, Integer seriesId, String videoId);

    void deleteSeriesContent(String userId, Integer seriesId, String videoId);
}
