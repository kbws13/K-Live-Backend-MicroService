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
 * 评论表
 *
 * @TableName videoComment
 */
@TableName(value = "videoComment")
@Data
public class VideoComment implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 父级评论 id
     */
    private Integer parentCommentId;
    /**
     * 视频 id
     */
    private String videoId;
    /**
     * 视频用户 id
     */
    private String videoUserId;
    /**
     * 回复内容
     */
    private String content;
    /**
     * 图片内容
     */
    private String imgPath;
    /**
     * 用户 id
     */
    private String userId;
    /**
     * 回复人 id
     */
    private String replyUserId;
    /**
     * 0: 未置顶 1:已置顶
     */
    private Integer topType;
    /**
     * 发布时间
     */
    private Date postTime;
    /**
     * 喜欢数量
     */
    private Integer likeCount;
    /**
     * 讨厌数量
     */
    private Integer hateCount;
    @TableField(exist = false)
    private String avatar;
    @TableField(exist = false)
    private String nickName;
    @TableField(exist = false)
    private String replyAvatar;
    @TableField(exist = false)
    private String replyNickName;
    @TableField(exist = false)
    private List<VideoComment> children;
    @TableField(exist = false)
    private String cover;
    @TableField(exist = false)
    private String videoName;
}