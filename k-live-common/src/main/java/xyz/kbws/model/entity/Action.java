package xyz.kbws.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户行为 点赞、评论
 *
 * @TableName action
 */
@TableName(value = "action")
@Data
public class Action implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 视频 id
     */
    private String videoId;
    /**
     * 视频用户 id
     */
    private String videoUserId;
    /**
     * 评论 id
     */
    private Integer commentId;
    /**
     * 0: 评论喜欢点赞 1:讨厌评论 2:视频点赞 3:视频收藏 4:视频投币
     */
    private Integer actionType;
    /**
     * 数量
     */
    private Integer count;
    /**
     * 用户 id
     */
    private String userId;
    /**
     * 操作时间
     */
    private Date actionTime;
    @TableField(exist = false)
    private String videoCover;
    @TableField(exist = false)
    private String videoName;
}