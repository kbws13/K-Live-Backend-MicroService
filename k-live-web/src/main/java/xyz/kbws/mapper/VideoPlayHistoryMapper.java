package xyz.kbws.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import xyz.kbws.model.dto.videoPlayHistory.VideoHistoryQueryRequest;
import xyz.kbws.model.entity.VideoPlayHistory;

import java.util.List;

/**
 * @author fangyuan
 * @description 针对表【videoPlayHistory(视频播放历史表)】的数据库操作Mapper
 * @createDate 2024-12-15 12:09:56
 * @Entity generator.domain.VideoPlayHistory
 */
public interface VideoPlayHistoryMapper extends BaseMapper<VideoPlayHistory> {

    List<VideoPlayHistory> selectList(@Param("query") VideoHistoryQueryRequest query);
}




