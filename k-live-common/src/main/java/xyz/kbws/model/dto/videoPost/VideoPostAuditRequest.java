package xyz.kbws.model.dto.videoPost;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * @author kbws
 * @date 2024/12/5
 * @description: 视频审核处理请求
 */
@Data
public class VideoPostAuditRequest implements Serializable {

    private static final long serialVersionUID = -5281628727697838530L;
    @NotEmpty(message = "视频 id 不能为空")
    private String videoId;
    @NotEmpty(message = "状态不能为空")
    private Integer status;
    private String reason;
}
