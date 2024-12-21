package xyz.kbws.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import xyz.kbws.model.dto.videoPost.VideoPostQueryRequest;
import xyz.kbws.model.entity.VideoPost;
import xyz.kbws.model.vo.VideoPostVO;

import java.util.List;

/**
 * @author fangyuan
 * @description 针对表【videoPost(已发布视频信息表)】的数据库操作Mapper
 * @createDate 2024-11-28 20:36:20
 * @Entity generator.domain.Videopost
 */
public interface VideoPostMapper extends BaseMapper<VideoPost> {

    List<VideoPostVO> loadVideoPost(@Param("query") VideoPostQueryRequest query, @Param("userId") String userId);
}




