package xyz.kbws.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 视频合集表
 *
 * @TableName series
 */
@TableName(value = "series")
@Data
public class Series implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 合集 id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 合集名称
     */
    private String name;
    /**
     * 合集描述
     */
    private String description;
    /**
     * 用户 id
     */
    private String userId;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 视频封面
     */
    @TableField(exist = false)
    private String cover;
    /**
     * 合集中的视频
     */
    @TableField(exist = false)
    private List<Video> videoList;
}