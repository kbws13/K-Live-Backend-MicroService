package xyz.kbws.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.kbws.common.DeleteRequest;
import xyz.kbws.common.ErrorCode;
import xyz.kbws.constant.UserConstant;
import xyz.kbws.exception.BusinessException;
import xyz.kbws.mapper.CategoryMapper;
import xyz.kbws.model.dto.category.CategoryAddRequest;
import xyz.kbws.model.dto.category.CategoryChangeSortRequest;
import xyz.kbws.model.dto.category.CategoryQueryRequest;
import xyz.kbws.model.dto.category.CategoryUpdateRequest;
import xyz.kbws.model.entity.Category;
import xyz.kbws.model.entity.Video;
import xyz.kbws.redis.RedisComponent;
import xyz.kbws.service.CategoryService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author fangyuan
 * @description 针对表【category(分类表)】的数据库操作Service实现
 * @createDate 2024-11-27 00:11:01
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>
        implements CategoryService {

    @Resource
    private CategoryMapper categoryMapper;

    //@Resource
    //private VideoService videoService;

    @Resource
    private RedisComponent redisComponent;

    @Override
    public List<Category> getAllCategory() {
        List<Category> categoryList = redisComponent.getCategoryList();
        if (categoryList == null || categoryList.isEmpty()) {
            save2Redis();
        }
        return redisComponent.getCategoryList();
    }

    @Override
    public List<Category> queryCategory(CategoryQueryRequest categoryQueryRequest) {
        QueryWrapper<Category> queryWrapper = getQueryWrapper(categoryQueryRequest);
        List<Category> list = this.list(queryWrapper);
        if (categoryQueryRequest.getCoverLine2Tree()) {
            list = coverLine2Tree(list, UserConstant.ZERO);
        }
        return list;
    }

    private List<Category> coverLine2Tree(List<Category> dateList, Integer parentId) {
        List<Category> tree = new ArrayList<>();
        for (Category category : dateList) {
            if (category.getId() != null && category.getParentCategoryId() != null
                    && category.getParentCategoryId().equals(parentId)) {
                category.setChildren(coverLine2Tree(dateList, category.getId()));
                tree.add(category);
            }
        }
        return tree;
    }

    @Override
    public Boolean addCategory(CategoryAddRequest categoryAddRequest) {
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("code", categoryAddRequest.getCode());
        Category one = this.getOne(queryWrapper);
        if (one != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "分类编号已存在");
        }
        Category category = new Category();
        BeanUtil.copyProperties(categoryAddRequest, category);
        Integer maxSort = categoryMapper.selectMaxSort(categoryAddRequest.getParentCategoryId());
        category.setSort(maxSort + 1);
        boolean save = this.save(category);
        save2Redis();
        return save;
    }

    @Override
    public Boolean updateCategory(CategoryUpdateRequest categoryUpdateRequest) {
        Category category = this.getById(categoryUpdateRequest.getId());
        if (category == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "该分类不存在");
        }
        BeanUtil.copyProperties(categoryUpdateRequest, category);
        boolean res = this.updateById(category);
        save2Redis();
        return res;
    }

    @Override
    public Boolean deleteCategory(DeleteRequest deleteRequest) {
        Integer categoryId = deleteRequest.getId();
        // 查询分类下是否有视频
        QueryWrapper<Video> videoQueryWrapper = new QueryWrapper<>();
        videoQueryWrapper
                .eq("categoryId", categoryId)
                .or()
                .eq("parentCategoryId", categoryId);
        // TODO Web 模块提供获取分类下视频数量
        long count = 0;
        //long count = videoService.count(videoQueryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "分类下有视频，无法删除");
        }
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("id", categoryId)
                .or()
                .eq("parentCategoryId", categoryId);
        boolean remove = this.remove(queryWrapper);

        //  刷新缓存
        save2Redis();
        return remove;
    }

    @Override
    public Boolean changeSortCategory(CategoryChangeSortRequest categoryChangeSortRequest) {
        Integer parentCategoryId = categoryChangeSortRequest.getParentCategoryId();
        String[] categoryIdArray = categoryChangeSortRequest.getCategoryIds().split(",");
        List<Category> categoryList = new ArrayList<>();
        int sort = 1;
        for (String categoryId : categoryIdArray) {
            Category category = new Category();
            category.setId(Integer.parseInt(categoryId));
            category.setParentCategoryId(parentCategoryId);
            category.setSort(sort++);
            categoryList.add(category);
        }
        Boolean res = categoryMapper.updateSortBatch(categoryList);
        // 刷新缓存
        save2Redis();
        return res;
    }

    @Override
    public QueryWrapper<Category> getQueryWrapper(CategoryQueryRequest categoryQueryRequest) {
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        String code = categoryQueryRequest.getCode();
        String name = categoryQueryRequest.getName();
        Integer parentCategoryId = categoryQueryRequest.getParentCategoryId();
        Integer sort = categoryQueryRequest.getSort();
        queryWrapper.eq(StrUtil.isNotBlank(code), "code", code);
        queryWrapper.like(StrUtil.isNotBlank(name), "name", name);
        queryWrapper.eq(parentCategoryId != null, "parentCategoryId", parentCategoryId);
        queryWrapper.eq(sort != null, "sort", sort);
        queryWrapper.orderByAsc("id");
        return queryWrapper;
    }

    private void save2Redis() {
        List<Category> list = this.list();
        list = coverLine2Tree(list, UserConstant.ZERO);
        redisComponent.saveCategoryList(list);
    }
}




