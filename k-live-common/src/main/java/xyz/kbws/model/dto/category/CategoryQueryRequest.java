package xyz.kbws.model.dto.category;

import lombok.Data;

import java.io.Serializable;

/**
 * @author kbws
 * @date 2024/11/27
 * @description: 分类查询请求
 */
@Data
public class CategoryQueryRequest implements Serializable {

    private static final long serialVersionUID = 1399995342149613171L;
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
     * 排序号
     */
    private Integer sort;
    /**
     * 是否转换成树形
     */
    private Boolean coverLine2Tree;
}
