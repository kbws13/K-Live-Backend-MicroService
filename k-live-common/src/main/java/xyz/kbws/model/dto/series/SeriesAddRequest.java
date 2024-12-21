package xyz.kbws.model.dto.series;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @author kbws
 * @date 2024/12/11
 * @description: 添加合集请求
 */
@Data
public class SeriesAddRequest implements Serializable {

    private static final long serialVersionUID = -5914238821367236627L;
    @NotEmpty(message = "合集名称不能为空")
    @Size(max = 100, message = "名称超过最大长度")
    private String name;
    @Size(max = 200, message = "简介超过最大长度")
    private String description;
    private String videoIds;
}
