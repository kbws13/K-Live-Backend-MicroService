package xyz.kbws.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户表
 *
 * @TableName user
 */
@TableName(value = "user")
@Data
public class User implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 用户 id
     */
    @TableId
    private String id;
    /**
     * 昵称
     */
    private String nickName;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 密码
     */
    private String password;
    /**
     * 0:女 1:男 2:未知
     */
    private Integer sex;
    /**
     * 出生日期
     */
    private String birthday;
    /**
     * 学校
     */
    private String school;
    /**
     * 个人简介
     */
    private String personIntroduction;
    /**
     * 最后登录时间
     */
    private Date lastLoginTime;
    /**
     * 最后登录 IP
     */
    private String lastLoginIp;
    /**
     * 用户角色：user/admin/ban
     */
    private String userRole;
    /**
     * 空间公告
     */
    private String noticeInfo;
    /**
     * 硬币总数量
     */
    private Integer totalCoinCount;
    /**
     * 当前硬币总数量
     */
    private Integer currentCoinCount;
    /**
     * 主题
     */
    private Integer theme;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 是否删除
     */
    private Integer isDelete;
}