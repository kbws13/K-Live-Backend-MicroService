package xyz.kbws.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import xyz.kbws.model.entity.Category;

import java.util.List;

/**
 * @author fangyuan
 * @description 针对表【category(分类表)】的数据库操作Mapper
 * @createDate 2024-11-27 00:11:01
 * @Entity generator.domain.Category
 */
public interface CategoryMapper extends BaseMapper<Category> {

    Integer selectMaxSort(@Param("parentCategoryId") Integer parentCategoryId);

    Boolean updateSortBatch(@Param("categoryList") List<Category> categoryList);
}




