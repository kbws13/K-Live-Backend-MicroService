package xyz.kbws.model.vo;

import lombok.Data;
import xyz.kbws.model.entity.Action;
import xyz.kbws.model.entity.Video;

import java.io.Serializable;
import java.util.List;

/**
 * @author kbws
 * @date 2024/12/6
 * @description: 视频详情 VO
 */
@Data
public class VideoInfoResultVO implements Serializable {

    private static final long serialVersionUID = -6559627645429069138L;
    private Video video;
    private List<Action> userActionList;
}
