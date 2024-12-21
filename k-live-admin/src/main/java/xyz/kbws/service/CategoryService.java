package xyz.kbws.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import xyz.kbws.common.DeleteRequest;
import xyz.kbws.model.dto.category.CategoryAddRequest;
import xyz.kbws.model.dto.category.CategoryChangeSortRequest;
import xyz.kbws.model.dto.category.CategoryQueryRequest;
import xyz.kbws.model.dto.category.CategoryUpdateRequest;
import xyz.kbws.model.entity.Category;

import java.util.List;

/**
 * @author fangyuan
 * @description 针对表【category(分类表)】的数据库操作Service
 * @createDate 2024-11-27 00:11:01
 */
public interface CategoryService extends IService<Category> {

    List<Category> getAllCategory();

    List<Category> queryCategory(CategoryQueryRequest categoryQueryRequest);

    Boolean addCategory(CategoryAddRequest categoryAddRequest);

    Boolean updateCategory(CategoryUpdateRequest categoryUpdateRequest);

    Boolean deleteCategory(DeleteRequest deleteRequest);

    Boolean changeSortCategory(CategoryChangeSortRequest categoryChangeSortRequest);

    QueryWrapper<Category> getQueryWrapper(CategoryQueryRequest categoryQueryRequest);
}
