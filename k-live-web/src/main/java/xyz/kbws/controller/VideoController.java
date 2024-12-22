package xyz.kbws.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import xyz.kbws.api.consumer.InteractClient;
import xyz.kbws.common.BaseResponse;
import xyz.kbws.common.ErrorCode;
import xyz.kbws.common.ResultUtils;
import xyz.kbws.constant.CommonConstant;
import xyz.kbws.es.EsComponent;
import xyz.kbws.exception.BusinessException;
import xyz.kbws.mapper.VideoMapper;
import xyz.kbws.model.dto.video.VideoQueryRequest;
import xyz.kbws.model.dto.video.VideoReportRequest;
import xyz.kbws.model.entity.Action;
import xyz.kbws.model.entity.Video;
import xyz.kbws.model.entity.VideoFile;
import xyz.kbws.model.enums.SearchOrderTypeEnum;
import xyz.kbws.model.enums.UserActionTypeEnum;
import xyz.kbws.model.enums.VideoRecommendTypeEnum;
import xyz.kbws.model.vo.UserVO;
import xyz.kbws.model.vo.VideoInfoResultVO;
import xyz.kbws.redis.RedisComponent;
import xyz.kbws.service.VideoFileService;
import xyz.kbws.service.VideoService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author kbws
 * @date 2024/12/21
 * @description: 视频接口
 */
@Api(tags = "视频接口")
@RestController
@RequestMapping("/video")
public class VideoController {
    @Resource
    private VideoService videoService;

    @Resource
    private VideoFileService videoFileService;

    @Resource
    private InteractClient interactClient;

    @Resource
    private VideoMapper videoMapper;

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private EsComponent esComponent;

    @ApiOperation(value = "获取推荐视频列表")
    @GetMapping("/loadRecommendVideo")
    public BaseResponse<List<Video>> loadRecommendVideo() {
        VideoQueryRequest videoQueryRequest = new VideoQueryRequest();
        videoQueryRequest.setQueryUserInfo(true);
        videoQueryRequest.setSortField("createTime");
        videoQueryRequest.setSortOrder(CommonConstant.SORT_ORDER_DESC);
        videoQueryRequest.setRecommendType(VideoRecommendTypeEnum.RECOMMEND.getValue());
        List<Video> list = videoMapper.queryList(videoQueryRequest);
        return ResultUtils.success(list);
    }

    @ApiOperation(value = "获取非推荐视频列表")
    @PostMapping("/loadVideoList")
    public BaseResponse<Page<Video>> loadVideoList(@RequestBody VideoQueryRequest videoQueryRequest) {
        long current = videoQueryRequest.getCurrent();
        long pageSize = videoQueryRequest.getPageSize();
        videoQueryRequest.setRecommendType(VideoRecommendTypeEnum.NO_RECOMMEND.getValue());
        videoQueryRequest.setQueryUserInfo(true);
        List<Video> record = videoMapper.queryList(videoQueryRequest);
        Page<Video> page = new Page<>();
        page.setRecords(record);
        page.setSize(record.size());
        page.setCurrent(current);
        page.setSize(pageSize);
        return ResultUtils.success(page);
    }

    @ApiOperation(value = "获取视频信息")
    @GetMapping("/getVideoInfo")
    public BaseResponse<VideoInfoResultVO> getVideoInfo(@NotEmpty(message = "视频 id 不能为空") String videoId, HttpServletRequest request) {
        Video video = videoService.getById(videoId);
        if (video == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "该视频不存在");
        }
        // 调用互动模块获取用户点赞、投币、收藏
        List<Action> list = new ArrayList<>();
        String token = request.getHeader("token");
        UserVO userVO = redisComponent.getUserVO(token);
        if (userVO != null) {
            QueryWrapper<Action> queryWrapper = new QueryWrapper<>();
            List<Integer> types = Arrays.asList(UserActionTypeEnum.VIDEO_LIKE.getValue(), UserActionTypeEnum.VIDEO_COLLECT.getValue(), UserActionTypeEnum.VIDEO_COIN.getValue());
            queryWrapper.eq("videoId", videoId)
                    .eq("userId", userVO.getId())
                    .in("actionType", types);
            list = interactClient.list(queryWrapper);
        }
        VideoInfoResultVO videoInfoResultVO = new VideoInfoResultVO();
        videoInfoResultVO.setVideo(video);
        videoInfoResultVO.setUserActionList(list);
        return ResultUtils.success(videoInfoResultVO);
    }

    @ApiOperation(value = "获取视频分集列表")
    @GetMapping("/loadVideoPList")
    public BaseResponse<List<VideoFile>> loadVideoPList(@NotEmpty(message = "视频 id 不能为空") String videoId) {
        QueryWrapper<VideoFile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("videoId", videoId);
        queryWrapper.orderByAsc("fileIndex");
        List<VideoFile> list = videoFileService.list(queryWrapper);
        return ResultUtils.success(list);
    }

    @ApiOperation(value = "上报在线观看人数")
    @PostMapping("/reportVideoPlayOnline")
    public BaseResponse<Integer> repostVideoPlayOnline(@RequestBody VideoReportRequest videoReportRequest) {
        Integer count = redisComponent.reportVideoPlayOnline(videoReportRequest.getFileId(), videoReportRequest.getDeviceId());
        return ResultUtils.success(count);
    }

    @ApiOperation(value = "搜索视频")
    @GetMapping("/search")
    public BaseResponse<Page<Video>> search(@NotEmpty String keyword, Integer orderType, Integer current) {
        //  记录搜索热词
        redisComponent.addKeywordCount(keyword);
        Page<Video> page = esComponent.search(true, keyword, orderType, current, 30);
        return ResultUtils.success(page);
    }

    @ApiOperation(value = "获取推荐视频")
    @GetMapping("/getRecommendVideo")
    public BaseResponse<List<Video>> getRecommendVideo(@NotEmpty String keyword, @NotEmpty String videoId) {
        List<Video> records = esComponent.search(true, keyword, SearchOrderTypeEnum.VIDEO_PLAY.getValue(), 1, 10).getRecords();
        records = records.stream().filter(item -> item.getId().equals(videoId)).collect(Collectors.toList());
        return ResultUtils.success(records);
    }

    @ApiOperation(value = "获取搜索热词")
    @GetMapping("/getSearchKeywordTop")
    public BaseResponse<List<String>> getSearchKeywordTop() {
        List<String> keywordTop = redisComponent.getKeywordTop(10);
        return ResultUtils.success(keywordTop);
    }

    @ApiOperation(value = "获取热门播放视频")
    @GetMapping("/loadHotVideoList")
    public BaseResponse<Page<Video>> loadHotVideoList(@RequestBody VideoQueryRequest videoQueryRequest) {
        videoQueryRequest.setQueryUserInfo(true);
        videoQueryRequest.setLastPlayHour(CommonConstant.HOUR_24);
        List<Video> videoList = videoService.selectList(videoQueryRequest);
        Page<Video> page = new Page<>();
        page.setRecords(videoList);
        page.setCurrent(videoQueryRequest.getCurrent());
        page.setSize(videoQueryRequest.getPageSize());
        page.setTotal(videoList.size());
        return ResultUtils.success(page);
    }
}
