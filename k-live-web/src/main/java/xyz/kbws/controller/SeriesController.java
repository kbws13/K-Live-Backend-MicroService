package xyz.kbws.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import xyz.kbws.annotation.AuthCheck;
import xyz.kbws.common.BaseResponse;
import xyz.kbws.common.ErrorCode;
import xyz.kbws.common.ResultUtils;
import xyz.kbws.constant.CommonConstant;
import xyz.kbws.exception.BusinessException;
import xyz.kbws.model.dto.series.SeriesAddRequest;
import xyz.kbws.model.dto.series.SeriesUpdateRequest;
import xyz.kbws.model.entity.Series;
import xyz.kbws.model.entity.SeriesContent;
import xyz.kbws.model.entity.Video;
import xyz.kbws.model.query.SeriesContentQuery;
import xyz.kbws.model.vo.SeriesDetailVO;
import xyz.kbws.model.vo.UserVO;
import xyz.kbws.redis.RedisComponent;
import xyz.kbws.service.SeriesContentService;
import xyz.kbws.service.SeriesService;
import xyz.kbws.service.VideoService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author kbws
 * @date 2024/12/21
 * @description: 视频合集接口
 */
@Api(tags = "视频合集接口")
@RestController
@RequestMapping("/series")
public class SeriesController {
    @Resource
    private VideoService videoService;

    @Resource
    private SeriesService seriesService;

    @Resource
    private SeriesContentService seriesContentService;

    @Resource
    private RedisComponent redisComponent;

    @ApiOperation(value = "获取用户所有视频合集")
    @GetMapping("/loadSeries")
    public BaseResponse<List<Series>> loadSeries(@NotEmpty String userId) {
        List<Series> userAllSeries = seriesService.getUserAllSeries(userId);
        return ResultUtils.success(userAllSeries);
    }

    @ApiOperation(value = "查询主页集合视频")
    @GetMapping("/loadSeriesWithVideo")
    public BaseResponse<List<Series>> loadSeriesWithVideo(@NotEmpty String userId) {
        List<Series> seriesList = seriesService.selectListWithVideoList(userId);
        return ResultUtils.success(seriesList);
    }

    @ApiOperation(value = "添加视频合集")
    @AuthCheck
    @PostMapping("/add")
    public void addSeries(@RequestBody SeriesAddRequest seriesAddRequest, HttpServletRequest request) {
        String token = request.getHeader("token");
        UserVO userVO = redisComponent.getUserVO(token);
        Series series = new Series();
        series.setName(seriesAddRequest.getName());
        series.setDescription(seriesAddRequest.getDescription());
        series.setUserId(userVO.getId());
        seriesService.addSeries(series, seriesAddRequest.getVideoIds());
    }

    @ApiOperation(value = "更改合集排序")
    @AuthCheck
    @PostMapping("/changeSeriesSort")
    public void changeSeriesSort(@NotEmpty String seriesIds, HttpServletRequest request) {
        String token = request.getHeader("token");
        UserVO userVO = redisComponent.getUserVO(token);
        seriesService.changeSeriesSort(userVO.getId(), seriesIds);
    }

    @ApiOperation(value = "删除视频合集")
    @AuthCheck
    @PostMapping("/deleteSeries")
    public void deleteSeries(@NotNull Integer seriesId, HttpServletRequest request) {
        String token = request.getHeader("token");
        UserVO userVO = redisComponent.getUserVO(token);
        seriesService.deleteSeries(userVO.getId(), seriesId);
    }

    @ApiOperation(value = "更新视频合集")
    @AuthCheck
    @PostMapping("/update")
    public void updateSeries(@RequestBody SeriesUpdateRequest seriesUpdateRequest, HttpServletRequest request) {
        String token = request.getHeader("token");
        UserVO userVO = redisComponent.getUserVO(token);
        Series series = new Series();
        BeanUtil.copyProperties(seriesUpdateRequest, series);
        series.setUserId(userVO.getId());
        seriesService.updateById(series);
    }

    @ApiOperation(value = "获取所有视频")
    @GetMapping("/loadAllVideo")
    public BaseResponse<List<Video>> loadAllVideo(Integer seriesId, HttpServletRequest request) {
        String token = request.getHeader("token");
        UserVO userVO = redisComponent.getUserVO(token);
        QueryWrapper<Video> videoQueryWrapper = new QueryWrapper<>();
        if (seriesId != null) {
            QueryWrapper<SeriesContent> seriesContentQueryWrapper = new QueryWrapper<>();
            seriesContentQueryWrapper.eq("seriesId", seriesId)
                    .eq("userId", userVO.getId());
            List<SeriesContent> seriesContentList = seriesContentService.list(seriesContentQueryWrapper);
            List<String> videoIdList = seriesContentList.stream()
                    .map(SeriesContent::getVideoId)
                    .collect(Collectors.toList());
            videoQueryWrapper.notIn("id", videoIdList);
        }
        videoQueryWrapper.eq("userId", userVO.getId());
        List<Video> list = videoService.list(videoQueryWrapper);
        return ResultUtils.success(list);
    }

    @ApiOperation(value = "获取合集详情")
    @GetMapping("/getSeriesDetail")
    public BaseResponse<SeriesDetailVO> getSeriesDetail(@NotNull Integer seriesId) {
        Series series = seriesService.getById(seriesId);
        if (series == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        SeriesContentQuery seriesContentQuery = new SeriesContentQuery();
        seriesContentQuery.setQueryVideo(true);
        seriesContentQuery.setSeriesId(seriesId);
        seriesContentQuery.setSortField("sort");
        seriesContentQuery.setSortOrder(CommonConstant.SORT_ORDER_ASC);
        List<SeriesContent> seriesContentList = seriesContentService.selectList(seriesContentQuery);
        SeriesDetailVO seriesDetailVO = new SeriesDetailVO();
        seriesDetailVO.setSeries(series);
        seriesDetailVO.setSeriesContentList(seriesContentList);
        return ResultUtils.success(seriesDetailVO);
    }

    @ApiOperation(value = "合集添加视频")
    @AuthCheck
    @PostMapping("/saveSeriesContent")
    public void saveSeriesContent(@NotNull Integer seriesId, @NotEmpty String videoId, HttpServletRequest request) {
        String token = request.getHeader("token");
        UserVO userVO = redisComponent.getUserVO(token);
        seriesService.saveSeriesContent(userVO.getId(), seriesId, videoId);
    }

    @ApiOperation(value = "删除合集中视频")
    @AuthCheck
    @PostMapping("/deleteSeriesVideo")
    public void deleteSeriesVideo(@NotNull Integer seriesId, @NotEmpty String videoId, HttpServletRequest request) {
        String token = request.getHeader("token");
        UserVO userVO = redisComponent.getUserVO(token);
        seriesService.deleteSeriesContent(userVO.getId(), seriesId, videoId);
    }
}
