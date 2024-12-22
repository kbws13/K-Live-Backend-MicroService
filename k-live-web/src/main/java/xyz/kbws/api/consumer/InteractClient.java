package xyz.kbws.api.consumer;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import xyz.kbws.model.entity.Action;
import xyz.kbws.model.entity.Danmu;
import xyz.kbws.model.entity.VideoComment;

import java.util.List;

/**
 * @author kbws
 * @date 2024/12/21
 * @description: 分类接口客户端
 */
@FeignClient(name = "k-live-interact")
public interface InteractClient {

    @PostMapping("/inner/action/list")
    List<Action> list(@RequestBody QueryWrapper<Action> queryWrapper);

    @PostMapping("/inner/danmu/deleteVideoDanmu")
    void deleteDanmu(@RequestBody QueryWrapper<Danmu> danmuQueryWrapper);

    @PostMapping("/inner/videoComment/deleteVideoComment")
    void deleteVideoComment(@RequestBody QueryWrapper<VideoComment> videoCommentQueryWrapper);
}
