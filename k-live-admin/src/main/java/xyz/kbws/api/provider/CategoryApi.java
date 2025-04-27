package xyz.kbws.api.provider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.kbws.model.entity.Category;
import xyz.kbws.service.CategoryService;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author kbws
 * @date 2024/12/21
 * @description: 分类接口提供者
 */
@Slf4j
@RestController
@RequestMapping("/inner")
public class CategoryApi {

    @Resource
    private CategoryService categoryService;

    @GetMapping("/getAllCategory")
    public List<Category> getAllCategory() {
        List<Category> allCategory = categoryService.getAllCategory();
        log.info("Redis 中的分类列表: {}", allCategory);
        return allCategory;
    }
}
