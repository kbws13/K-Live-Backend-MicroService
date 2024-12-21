package xyz.kbws.model.dto.category;

import lombok.Data;

import java.io.Serializable;

/**
 * @author kbws
 * @date 2024/11/27
 * @description: 更改分类排序请求
 */
@Data
public class CategoryChangeSortRequest implements Serializable {

    private static final long serialVersionUID = -8758349403097026855L;
    private Integer parentCategoryId;
    private String categoryIds;
}
