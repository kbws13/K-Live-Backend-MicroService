package xyz.kbws.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import xyz.kbws.model.entity.VideoComment;
import xyz.kbws.model.query.VideoCommentQuery;

import java.util.List;

/**
 * @author fangyuan
 * @description 针对表【videoComment(评论表)】的数据库操作Mapper
 * @createDate 2024-12-07 12:14:12
 * @Entity generator.domain.Videocomment
 */
public interface VideoCommentMapper extends BaseMapper<VideoComment> {

    List<VideoComment> selectList(@Param("query") VideoCommentQuery query);

    List<VideoComment> selectListWithChildren(@Param("query") VideoCommentQuery query);

    void updateCount(@Param("id") Integer id, @Param("field") String field, @Param("changeCount") Integer changeCount,
                     @Param("opposeField") String opposeField, @Param("opposeChangeCount") Integer opposeChangeCount);

    Integer selectCount(@Param("query") VideoCommentQuery query);
}




