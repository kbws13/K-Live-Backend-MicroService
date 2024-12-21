package xyz.kbws.model.dto.video;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

/**
 * @author kbws
 * @date 2024/12/14
 * @description: 视频播放记录请求
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VideoPlayRequest implements Serializable {

    private static final long serialVersionUID = 5226611920468138498L;
    private String videoId;
    private String userId;
    private Integer fileIndex;
}
