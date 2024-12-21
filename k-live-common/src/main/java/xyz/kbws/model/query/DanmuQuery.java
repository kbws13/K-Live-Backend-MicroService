package xyz.kbws.model.query;

import lombok.Data;
import xyz.kbws.common.PageRequest;

import java.io.Serializable;

/**
 * @author kbws
 * @date 2024/12/13
 * @description:
 */
@Data
public class DanmuQuery extends PageRequest implements Serializable {

    private static final long serialVersionUID = 7983922646304381738L;
    private String videoId;
    private String userId;
    private Boolean queryVideoInfo;
    private String videoNameFuzzy;
}
