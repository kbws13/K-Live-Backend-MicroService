package xyz.kbws.model.dto.videoPost;

import lombok.Data;
import xyz.kbws.common.PageRequest;

import java.io.Serializable;
import java.util.List;

/**
 * @author kbws
 * @date 2024/11/30
 * @description: 查询稿件请求
 */
@Data
public class VideoPostQueryRequest extends PageRequest implements Serializable {
    private static final long serialVersionUID = -5819807148578440459L;
    /**
     * 父分类 id
     */
    private Integer parentCategoryId;
    private Integer status;
    private String videoName;
    private Boolean queryCount;
    private Boolean queryUserInfo;
    private List<Integer> excludeStatus;
}
