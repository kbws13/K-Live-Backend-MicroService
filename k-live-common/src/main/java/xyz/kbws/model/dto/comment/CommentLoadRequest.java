package xyz.kbws.model.dto.comment;

import lombok.Data;
import xyz.kbws.common.PageRequest;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * @author kbws
 * @date 2024/12/7
 * @description: 加载评论请求
 */
@Data
public class CommentLoadRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = 7786912850978144296L;
    @NotEmpty(message = "视频 id 不能为空")
    private String videoId;
    private String videoName;
    private Integer orderType;
}
