package xyz.kbws.model.dto.home;

import lombok.Data;
import xyz.kbws.common.PageRequest;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * @author kbws
 * @date 2024/12/11
 * @description: 查询主页视频请求
 */
@Data
public class HomeLoadVideoRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = -2353028002558441797L;
    @NotEmpty(message = "用户 id 不能为空")
    private String userId;
    private Integer type;
    private String videoName;
    private Integer orderType;
}
