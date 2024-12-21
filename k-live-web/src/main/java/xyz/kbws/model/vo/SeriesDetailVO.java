package xyz.kbws.model.vo;

import lombok.Data;
import xyz.kbws.model.entity.Series;
import xyz.kbws.model.entity.SeriesContent;

import java.io.Serializable;
import java.util.List;

/**
 * @author kbws
 * @date 2024/12/11
 * @description:
 */
@Data
public class SeriesDetailVO implements Serializable {

    private static final long serialVersionUID = 4208386275632303370L;
    private Series series;
    private List<SeriesContent> seriesContentList;
}
