package xyz.kbws.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.kbws.mapper.VideoPlayHistoryMapper;
import xyz.kbws.model.dto.videoPlayHistory.VideoHistoryQueryRequest;
import xyz.kbws.model.entity.VideoPlayHistory;
import xyz.kbws.service.VideoPlayHistoryService;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author fangyuan
 * @description 针对表【videoPlayHistory(视频播放历史表)】的数据库操作Service实现
 * @createDate 2024-12-15 12:09:56
 */
@Service
public class VideoPlayHistoryServiceImpl extends ServiceImpl<VideoPlayHistoryMapper, VideoPlayHistory>
        implements VideoPlayHistoryService {

    @Resource
    private VideoPlayHistoryMapper videoPlayHistoryMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveHistory(String userId, String videoId, Integer fileIndex) {
        VideoPlayHistory videoPlayHistory = new VideoPlayHistory();
        videoPlayHistory.setUserId(userId);
        videoPlayHistory.setVideoId(videoId);
        videoPlayHistory.setFileIndex(fileIndex);
        videoPlayHistory.setLastUpdateTime(DateUtil.date());
        this.saveOrUpdate(videoPlayHistory);
    }

    @Override
    public List<VideoPlayHistory> selectList(VideoHistoryQueryRequest videoHistoryQueryRequest) {
        return videoPlayHistoryMapper.selectList(videoHistoryQueryRequest);
    }
}




