package xyz.kbws.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 视频文件信息表
 *
 * @TableName videoFile
 */
@TableName(value = "videoFile")
@Data
public class VideoFile implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 文件 id
     */
    @TableId
    private String fileId;
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
     * 持续时间(秒)
     */
    private Integer duration;
}