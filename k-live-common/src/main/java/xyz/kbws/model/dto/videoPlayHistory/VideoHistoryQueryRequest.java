package xyz.kbws.model.dto.videoPlayHistory;

import lombok.Data;
import xyz.kbws.common.PageRequest;

import java.io.Serializable;

/**
 * @author kbws
 * @date 2024/12/16
 * @description:
 */
@Data
public class VideoHistoryQueryRequest extends PageRequest implements Serializable {

    private String userId;

    private Boolean queryVideoInfo;
}
