package xyz.kbws.model.dto.comment;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @author kbws
 * @date 2024/12/7
 * @description: 发布评论请求
 */
@Data
public class CommentAddRequest implements Serializable {

    private static final long serialVersionUID = -6939784956670169171L;
    @NotEmpty(message = "视频 id 不能为空")
    private String videoId;
    @NotEmpty(message = "评论内容不能为空")
    @Size(max = 500, message = "评论内容过长")
    private String content;
    private Integer replyCommentId;
    @Size(max = 50)
    private String imgPath;
}
