package xyz.kbws.model.dto.danmu;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @author kbws
 * @date 2024/12/7
 * @description: 发送弹幕请求
 */
@Data
public class DanmuPostRequest implements Serializable {

    private static final long serialVersionUID = -499246564492014665L;
    @NotEmpty(message = "视频 id 不能为空")
    private String videoId;
    @NotEmpty(message = "视频分 p 的 id 不能为空")
    private String fileId;
    @NotEmpty(message = "弹幕内容不能为空")
    @Size(max = 200, message = "弹幕内容过长")
    private String text;
    @NotNull(message = "弹幕位置不能为空")
    private Integer model;
    @NotEmpty(message = "弹幕颜色不能为空")
    private String color;
    @NotNull(message = "发布时间不能为空")
    private Integer time;
}
