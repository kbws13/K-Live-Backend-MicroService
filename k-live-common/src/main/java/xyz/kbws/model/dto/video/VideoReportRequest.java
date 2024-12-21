package xyz.kbws.model.dto.video;

import lombok.Data;

import java.io.Serializable;

/**
 * @author kbws
 * @date 2024/12/7
 * @description:
 */
@Data
public class VideoReportRequest implements Serializable {

    private static final long serialVersionUID = 7838232634254252322L;
    private String fileId;
    private String deviceId;
}
