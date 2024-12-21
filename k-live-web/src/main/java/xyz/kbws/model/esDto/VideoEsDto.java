package xyz.kbws.model.esDto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author kbws
 * @date 2024/12/14
 * @description:
 */
@Data
public class VideoEsDto {

    /**
     * 视频 id
     */
    private String id;

    /**
     * 视频封面
     */
    private String cover;
    /**
     * 视频名称
     */
    private String name;
    /**
     * 用户 id
     */
    private String userId;
    /**
     * 标签
     */
    private String tags;
    /**
     * 播放数量
     */
    private Integer playCount;
    /**
     * 弹幕数量
     */
    private Integer danmuCount;
    /**
     * 收藏数量
     */
    private Integer collectCount;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
