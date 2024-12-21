package xyz.kbws.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import xyz.kbws.model.entity.VideoFilePost;

import java.util.List;

/**
 * @author fangyuan
 * @description 针对表【videoFilePost(已发布视频文件信息表)】的数据库操作Mapper
 * @createDate 2024-11-28 20:36:17
 * @Entity generator.domain.Videofilepost
 */
public interface VideoFilePostMapper extends BaseMapper<VideoFilePost> {

    void deleteBatchByFileId(@Param("fileIdList") List<String> fileIdList, @Param("userId") String userId);

    Integer sumDuration(@Param("videoId") String videoId);
}




