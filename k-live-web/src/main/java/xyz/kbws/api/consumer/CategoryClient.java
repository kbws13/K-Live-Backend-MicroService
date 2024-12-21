package xyz.kbws.api.consumer;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import xyz.kbws.model.entity.Category;

import java.util.List;

/**
 * @author kbws
 * @date 2024/12/21
 * @description: 分类接口客户端
 */
@FeignClient(name = "k-live-admin")
public interface CategoryClient {

    @GetMapping("/inner/getAllCategory")
    List<Category> getAllCategory();
}
