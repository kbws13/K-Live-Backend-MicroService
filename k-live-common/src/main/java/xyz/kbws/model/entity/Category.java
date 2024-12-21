package xyz.kbws.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分类表
 *
 * @TableName category
 */
@TableName(value = "category")
@Data
public class Category implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 分类 id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
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
    /**
     * 排序号
     */
    private Integer sort;
    @TableField(exist = false)
    private List<Category> children;
}