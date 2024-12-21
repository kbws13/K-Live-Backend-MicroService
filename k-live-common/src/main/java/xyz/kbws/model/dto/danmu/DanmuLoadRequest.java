package xyz.kbws.model.dto.danmu;

import lombok.Data;
import xyz.kbws.common.PageRequest;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * @author kbws
 * @date 2024/12/7
 * @description: 加载弹幕请求
 */
@Data
public class DanmuLoadRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = 3278820721130985915L;
    @NotEmpty(message = "视频 id 不能为空")
    private String videoId;
    @NotEmpty(message = "视频分 p 的 id 不能为空")
    private String fileId;
}
