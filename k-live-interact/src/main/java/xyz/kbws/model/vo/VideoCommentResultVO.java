package xyz.kbws.model.vo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import xyz.kbws.model.entity.Action;
import xyz.kbws.model.entity.VideoComment;

import java.io.Serializable;
import java.util.List;

/**
 * @author kbws
 * @date 2024/12/7
 * @description:
 */
@Data
public class VideoCommentResultVO implements Serializable {

    private static final long serialVersionUID = 8679828211028458315L;
    private Page<VideoComment> page;
    private List<Action> actionList;
}
