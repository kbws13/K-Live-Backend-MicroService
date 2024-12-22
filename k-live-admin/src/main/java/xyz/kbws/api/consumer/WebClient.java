package xyz.kbws.api.consumer;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import xyz.kbws.entity.query.StatisticInfoQuery;
import xyz.kbws.model.dto.user.UserChangeStatusRequest;
import xyz.kbws.model.dto.user.UserLoadRequest;
import xyz.kbws.model.dto.videoPost.VideoPostQueryRequest;
import xyz.kbws.model.entity.StatisticInfo;
import xyz.kbws.model.entity.User;
import xyz.kbws.model.entity.VideoFilePost;
import xyz.kbws.model.vo.VideoPostVO;

import java.util.List;
import java.util.Map;

/**
 * @author kbws
 * @date 2024/12/21
 * @description:
 */
@FeignClient(name = "k-live-web", contextId = "webClient")
public interface WebClient {

    @PostMapping("/inner/video/recommendVideo")
    Boolean recommendVideo(@RequestParam String videoId);

    @PostMapping("/inner/video/deleteVideo")
    void deleteVideo(@RequestParam String videoId, @RequestParam String userId);

    @PostMapping("/inner/videoPost/loadVideoPost")
    List<VideoPostVO> loadVideoPost(@RequestBody VideoPostQueryRequest videoPostQueryRequest, @RequestParam String userId);

    @PostMapping("/inner/videoFilePost/selectVideoFileList")
    List<VideoFilePost> selectVideoFileList(@RequestBody QueryWrapper<VideoFilePost> queryWrapper);

    @PostMapping("/inner/videoFilePost/auditVideo")
    void auditVideo(@RequestParam String videoId, @RequestParam Integer status, @RequestParam String reason);

    @PostMapping("/inner/statistic/findListTotalInfo")
    List<StatisticInfo> findListTotalInfo(@RequestBody StatisticInfoQuery statisticInfoQuery);

    @PostMapping("/inner/statistic/getTotalStatistic")
    Map<String, Integer> getTotalStatistic(@RequestParam String userId);

    @PostMapping("/inner/statistic/list")
    List<StatisticInfo> list(@RequestBody QueryWrapper<StatisticInfo> queryWrapper);

    @PostMapping("/inner/statistic/findUserCountTotalInfo")
    List<StatisticInfo> findUserCountTotalInfo(@RequestBody StatisticInfoQuery query);

    @PostMapping("/inner/user/count")
    Integer count();

    @PostMapping("/inner/user/page")
    Page<User> page(@RequestBody UserLoadRequest userLoadRequest);

    @PostMapping("/inner/user/changeStatus")
    Boolean changeStatus(@RequestBody UserChangeStatusRequest userChangeStatusRequest);
}
