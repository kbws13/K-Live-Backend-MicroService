package xyz.kbws.model.dto.series;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @author kbws
 * @date 2024/12/11
 * @description: 视频合集更新请求
 */
@Data
public class SeriesUpdateRequest implements Serializable {

    private static final long serialVersionUID = 5988677186783688538L;
    @NotNull
    private Integer id;
    @NotEmpty(message = "合集名称不能为空")
    @Size(max = 100, message = "名称超过最大长度")
    private String name;
    @Size(max = 200, message = "简介超过最大长度")
    private String description;
    private String videoIds;
}
