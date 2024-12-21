package xyz.kbws.model.dto.videoPost;

import lombok.Data;
import xyz.kbws.model.entity.VideoFilePost;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * @author kbws
 * @date 2024/11/28
 * @description: 发布视频请求
 */
@Data
public class VideoPostAddRequest implements Serializable {

    private static final long serialVersionUID = -3930918810320108343L;
    @NotEmpty(message = "视频封面不能为空")
    private String cover;
    @NotEmpty(message = "视频名字不能为空")
    @Size(max = 100, message = "视频名字超出最大长度")
    private String name;
    @NotNull(message = "父级分类 id 不能为空")
    private Integer parentCategoryId;
    private Integer categoryId;
    @NotNull(message = "发布方式不能为空")
    private Integer postType;
    @NotEmpty(message = "标签不能为空")
    @Size(max = 300, message = "标签超出最大长度")
    private String tags;
    @Size(max = 2000, message = "简介超出最大长度")
    private String introduction;
    @Size(max = 3, message = "互动设置超出最大长度")
    private String interaction;
    @NotEmpty(message = "视频文件列表不能为空")
    private List<VideoFilePost> videoFilePosts;
}
