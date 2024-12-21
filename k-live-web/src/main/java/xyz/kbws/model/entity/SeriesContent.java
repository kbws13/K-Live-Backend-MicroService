package xyz.kbws.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 视频合集内容表
 *
 * @TableName seriesContent
 */
@TableName(value = "seriesContent")
@Data
public class SeriesContent implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 合集 id
     */
    private Integer seriesId;
    /**
     * 视频 id
     */
    private String videoId;
    /**
     * 用户 id
     */
    private String userId;
    /**
     * 排序
     */
    private Integer sort;
    @TableField(exist = false)
    private String cover;
    @TableField(exist = false)
    private String name;
    @TableField(exist = false)
    private Integer playCount;
    @TableField(exist = false)
    private Date createTime;
}