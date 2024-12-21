package xyz.kbws.model.query;

import lombok.Data;
import xyz.kbws.common.PageRequest;

/**
 * @author kbws
 * @date 2024/12/11
 * @description:
 */
@Data
public class SeriesContentQuery extends PageRequest {

    private Boolean queryVideo;

    private Integer seriesId;
}
