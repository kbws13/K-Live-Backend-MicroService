package xyz.kbws.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 关注表
 *
 * @TableName focus
 */
@TableName(value = "focus")
@Data
public class Focus implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 用户 id
     */
    private String userId;
    /**
     * 用户 id
     */
    private String focusUserId;
    /**
     * 关注时间
     */
    private Date focusTime;
}