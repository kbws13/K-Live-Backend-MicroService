package xyz.kbws.model.query;

import lombok.Data;
import xyz.kbws.common.PageRequest;

import javax.validation.constraints.NotEmpty;

/**
 * @author kbws
 * @date 2024/12/11
 * @description:
 */
@Data
public class ActionQuery extends PageRequest {

    /**
     * 用户 id
     */
    @NotEmpty
    private String userId;

    private Boolean queryVideo;
}
