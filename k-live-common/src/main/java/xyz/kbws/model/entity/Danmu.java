package xyz.kbws.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 弹幕表
 *
 * @TableName danmu
 */
@TableName(value = "danmu")
@Data
public class Danmu implements Serializable {
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
     * 视频文件 id
     */
    private String fileId;
    /**
     * 用户 id
     */
    private String userId;
    /**
     * 发送时间
     */
    private Date postTime;
    /**
     * 内容
     */
    private String text;
    /**
     * 弹幕位置
     */
    private Integer mode;
    /**
     * 颜色
     */
    private String color;
    /**
     * 展示时间
     */
    private Integer time;
    @TableField(exist = false)
    private String videoName;
    @TableField(exist = false)
    private String videoCover;
    @TableField(exist = false)
    private String nickName;
}