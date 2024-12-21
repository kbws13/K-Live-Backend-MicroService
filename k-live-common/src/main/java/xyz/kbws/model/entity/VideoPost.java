package xyz.kbws.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 已发布视频信息表
 *
 * @TableName videoPost
 */
@TableName(value = "videoPost")
@Data
public class VideoPost implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 视频 id
     */
    @TableId
    private String id;
    /**
     * 视频封面
     */
    private String cover;
    /**
     * 视频名称
     */
    private String name;
    /**
     * 用户 id
     */
    private String userId;
    /**
     * 分类 id
     */
    private Integer categoryId;
    /**
     * 父级分类 id
     */
    private Integer parentCategoryId;
    /**
     * 0:转码中 1:转码失败 2:待审核 3:审核成功 4:审核失败
     */
    private Integer status;
    /**
     * 0:自己制作 1:转载
     */
    private Integer postType;
    /**
     * 源资源说明
     */
    private String originInfo;
    /**
     * 标签
     */
    private String tags;
    /**
     * 简介
     */
    private String introduction;
    /**
     * 互动设置
     */
    private String interaction;
    /**
     * 持续时间(秒)
     */
    private Integer duration;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
}