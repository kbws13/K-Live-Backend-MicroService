package xyz.kbws.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 视频播放历史表
 *
 * @TableName videoPlayHistory
 */
@TableName(value = "videoPlayHistory")
@Data
public class VideoPlayHistory implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 用户 id
     */
    private String userId;
    /**
     * 视频 id
     */
    private String videoId;
    /**
     * 视频文件索引
     */
    private Integer fileIndex;
    /**
     * 最后更新时间
     */
    private Date lastUpdateTime;
    @TableField(exist = false)
    private String videoCover;
    @TableField(exist = false)
    private String videoName;
}