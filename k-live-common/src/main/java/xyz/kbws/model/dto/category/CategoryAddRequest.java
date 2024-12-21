package xyz.kbws.model.dto.category;

import lombok.Data;

import java.io.Serializable;

/**
 * @author kbws
 * @date 2024/11/27
 * @description: 分类添加请求
 */
@Data
public class CategoryAddRequest implements Serializable {

    private static final long serialVersionUID = -891811301968801613L;
    /**
     * 分类编码
     */
    private String code;
    /**
     * 分类名称
     */
    private String name;
    /**
     * 父级分类 id
     */
    private Integer parentCategoryId;
    /**
     * 图标
     */
    private String icon;
    /**
     * 背景图
     */
    private String background;
}
