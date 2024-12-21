package xyz.kbws.api.consumer;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import xyz.kbws.model.entity.Danmu;
import xyz.kbws.model.entity.VideoComment;
import xyz.kbws.model.query.DanmuQuery;
import xyz.kbws.model.query.VideoCommentQuery;

import java.util.List;

/**
 * @author kbws
 * @date 2024/12/21
 * @description:
 */
@FeignClient(name = "k-live-interact", contextId = "interactClient")
public interface InteractClient {

    @PostMapping("/inner/videoComment/listByParams")
    List<VideoComment> listByParams(@RequestBody VideoCommentQuery videoCommentQuery);

    @PostMapping("/inner/videoComment/deleteComment")
    Boolean deleteComment(@RequestParam Integer commentId, @RequestParam String userId);

    @PostMapping("/inner/danmu/selectListByParam")
    List<Danmu> selectListByParam(@RequestBody DanmuQuery danmuQuery);

    @PostMapping("/inner/danmu/deleteDanmu")
    void deleteDanmu(@RequestParam String userId, @RequestParam Integer danmuId);
}
