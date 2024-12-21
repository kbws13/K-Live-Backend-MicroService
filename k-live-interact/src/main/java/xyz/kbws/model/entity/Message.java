package xyz.kbws.model.entity;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import xyz.kbws.model.dto.message.MessageExtendDTO;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户消息表
 *
 * @TableName message
 */
@TableName(value = "message")
@Data
public class Message implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 消息 id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 用户 id
     */
    private String userId;
    /**
     * 视频 id
     */
    private String videoId;
    /**
     * 消息类型
     */
    private Integer type;
    /**
     * 发生人 id
     */
    private String sendUserId;
    /**
     * 0:未读 1:已读
     */
    private Integer readType;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 扩展消息
     */
    private String extendJson;
    @TableField(exist = false)
    private String sendUserAvatar;
    @TableField(exist = false)
    private String sendUserName;
    @TableField(exist = false)
    private String videoName;
    @TableField(exist = false)
    private String videoCover;
    @TableField(exist = false)
    private MessageExtendDTO messageExtendDTO;

    public MessageExtendDTO getMessageExtendDTO() {
        return StrUtil.isEmpty(extendJson) ? new MessageExtendDTO() : JSONUtil.toBean(extendJson, MessageExtendDTO.class);
    }
}