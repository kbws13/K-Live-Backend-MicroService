package xyz.kbws.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.kbws.model.dto.videoPlayHistory.VideoHistoryQueryRequest;
import xyz.kbws.model.entity.VideoPlayHistory;

import java.util.List;

/**
 * @author fangyuan
 * @description 针对表【videoPlayHistory(视频播放历史表)】的数据库操作Service
 * @createDate 2024-12-15 12:09:56
 */
public interface VideoPlayHistoryService extends IService<VideoPlayHistory> {

    void saveHistory(String userId, String videoId, Integer fileIndex);

    List<VideoPlayHistory> selectList(VideoHistoryQueryRequest videoHistoryQueryRequest);
}
