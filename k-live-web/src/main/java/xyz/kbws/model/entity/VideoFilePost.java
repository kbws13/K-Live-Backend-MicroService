package xyz.kbws.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 已发布视频文件信息表
 *
 * @TableName videoFilePost
 */
@TableName(value = "videoFilePost")
@Data
public class VideoFilePost implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 文件 id
     */
    @TableId
    private String fileId;
    /**
     * 上传 id
     */
    private String uploadId;
    /**
     * 用户 id
     */
    private String userId;
    /**
     * 视频 id
     */
    private String videoId;
    /**
     * 文件索引
     */
    private Integer fileIndex;
    /**
     * 文件名
     */
    private String fileName;
    /**
     * 文件大小
     */
    private Long fileSize;
    /**
     * 文件路径
     */
    private String filePath;
    /**
     * 0:无更新 1:有更新
     */
    private Integer updateType;
    /**
     * 0:转码中 1:转码成功 2:转码失败
     */
    private Integer transferResult;
    /**
     * 持续时间(秒)
     */
    private Integer duration;
}