package xyz.kbws.model.dto.action;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author kbws
 * @date 2024/12/7
 * @description: 用户操作请求
 */
@Data
public class ActionDoRequest implements Serializable {

    private static final long serialVersionUID = 265659362043769896L;
    @NotEmpty(message = "视频 id 不能为空")
    private String videoId;
    @NotNull(message = "操作方式不能为空")
    private Integer actionType;
    @Max(value = 2)
    @Min(value = 1)
    private Integer count;
    private Integer commentId = 0;
}
