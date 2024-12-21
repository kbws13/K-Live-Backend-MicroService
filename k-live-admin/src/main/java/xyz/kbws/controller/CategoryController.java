package xyz.kbws.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.kbws.annotation.AuthCheck;
import xyz.kbws.common.BaseResponse;
import xyz.kbws.common.DeleteRequest;
import xyz.kbws.common.ErrorCode;
import xyz.kbws.common.ResultUtils;
import xyz.kbws.constant.UserConstant;
import xyz.kbws.exception.ThrowUtils;
import xyz.kbws.model.dto.category.CategoryAddRequest;
import xyz.kbws.model.dto.category.CategoryChangeSortRequest;
import xyz.kbws.model.dto.category.CategoryQueryRequest;
import xyz.kbws.model.dto.category.CategoryUpdateRequest;
import xyz.kbws.model.entity.Category;
import xyz.kbws.service.CategoryService;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author kbws
 * @date 2024/11/26
 * @description: 分类接口
 */
@Api(tags = "分类接口")
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    @ApiOperation(value = "查询分类")
    @PostMapping("/query")
    public BaseResponse<List<Category>> queryCategory(@RequestBody CategoryQueryRequest categoryQueryRequest) {
        ThrowUtils.throwIf(categoryQueryRequest == null, ErrorCode.PARAMS_ERROR);
        List<Category> list = categoryService.queryCategory(categoryQueryRequest);
        return ResultUtils.success(list);
    }

    @ApiOperation(value = "添加分类")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/add")
    public BaseResponse<Boolean> addCategory(@RequestBody CategoryAddRequest categoryAddRequest) {
        ThrowUtils.throwIf(categoryAddRequest == null, ErrorCode.PARAMS_ERROR);
        Boolean res = categoryService.addCategory(categoryAddRequest);
        return ResultUtils.success(res);
    }

    @ApiOperation(value = "修改分类")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/update")
    public BaseResponse<Boolean> updateCategory(@RequestBody CategoryUpdateRequest categoryUpdateRequest) {
        ThrowUtils.throwIf(categoryUpdateRequest == null, ErrorCode.PARAMS_ERROR);
        Boolean res = categoryService.updateCategory(categoryUpdateRequest);
        return ResultUtils.success(res);
    }

    @ApiOperation(value = "删除分类")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteCategory(@RequestBody DeleteRequest deleteRequest) {
        ThrowUtils.throwIf(deleteRequest == null, ErrorCode.PARAMS_ERROR);
        boolean res = categoryService.deleteCategory(deleteRequest);
        return ResultUtils.success(res);
    }

    @ApiOperation(value = "修改分类排序")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/changeSort")
    public BaseResponse<Boolean> changeSortCategory(@RequestBody CategoryChangeSortRequest categoryChangeSortRequest) {
        boolean res = categoryService.changeSortCategory(categoryChangeSortRequest);
        return ResultUtils.success(res);
    }
}
