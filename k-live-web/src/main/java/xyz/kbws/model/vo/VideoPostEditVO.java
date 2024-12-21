package xyz.kbws.model.vo;

import lombok.Data;
import xyz.kbws.model.entity.VideoFilePost;
import xyz.kbws.model.entity.VideoPost;

import java.io.Serializable;
import java.util.List;

/**
 * @author kbws
 * @date 2024/12/13
 * @description: 视频发布信息
 */
@Data
public class VideoPostEditVO implements Serializable {

    private static final long serialVersionUID = -7720355675345369426L;
    private VideoPost videoPost;
    private List<VideoFilePost> videoFilePostList;
}
