package xyz.kbws.model.dto.video;

import lombok.Data;
import xyz.kbws.common.PageRequest;

import java.io.Serializable;

/**
 * @author kbws
 * @date 2024/11/30
 * @description: 查询稿件请求
 */
@Data
public class VideoQueryRequest extends PageRequest implements Serializable {
    private static final long serialVersionUID = -5819807148578440459L;
    /**
     * 父分类 id
     */
    private Integer parentCategoryId;
    private Integer categoryId;
    private Boolean queryUserInfo;
    private Integer recommendType;
    private Integer lastPlayHour;
}
