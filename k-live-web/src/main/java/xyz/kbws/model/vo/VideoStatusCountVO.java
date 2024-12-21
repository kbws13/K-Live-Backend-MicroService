package xyz.kbws.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author kbws
 * @date 2024/12/4
 * @description: 不同状态的视频数量
 */
@Data
public class VideoStatusCountVO implements Serializable {

    private static final long serialVersionUID = -4700647550779646475L;
    private Integer auditPassCount;
    private Integer auditFailCount;
    private Integer inProcessCount;
}
