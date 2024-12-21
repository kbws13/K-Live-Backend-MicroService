package xyz.kbws.model.query;

import lombok.Data;
import xyz.kbws.common.PageRequest;

/**
 * @author kbws
 * @date 2024/12/7
 * @description:
 */
@Data
public class VideoCommentQuery extends PageRequest {

    private String videoId;

    private String videoNameFuzzy;

    private String userId;

    private Integer parentCommandId;

    private Integer topType;

    private Boolean loadChildren;

    private Boolean queryVideoInfo;
}
