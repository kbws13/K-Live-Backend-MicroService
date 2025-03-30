package xyz.kbws.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import xyz.kbws.annotation.AuthCheck;
import xyz.kbws.common.BaseResponse;
import xyz.kbws.common.ErrorCode;
import xyz.kbws.common.ResultUtils;
import xyz.kbws.exception.BusinessException;
import xyz.kbws.mapper.VideoPostMapper;
import xyz.kbws.model.dto.videoPost.VideoPostAddRequest;
import xyz.kbws.model.dto.videoPost.VideoPostQueryRequest;
import xyz.kbws.model.dto.videoPost.VideoPostUpdateRequest;
import xyz.kbws.model.entity.Video;
import xyz.kbws.model.entity.VideoFilePost;
import xyz.kbws.model.entity.VideoPost;
import xyz.kbws.model.enums.VideoStatusEnum;
import xyz.kbws.model.vo.UserVO;
import xyz.kbws.model.vo.VideoPostEditVO;
import xyz.kbws.model.vo.VideoPostVO;
import xyz.kbws.model.vo.VideoStatusCountVO;
import xyz.kbws.redis.RedisComponent;
import xyz.kbws.service.VideoFilePostService;
import xyz.kbws.service.VideoPostService;
import xyz.kbws.service.VideoService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author kbws
 * @date 2024/12/21
 * @description: 创作中心接口
 */
@Api(tags = "创作中心接口")
@RestController
@RequestMapping("/createCenter")
public class CreateCenterController {
    @Resource
    private VideoPostService videoPostService;

    @Resource
    private VideoFilePostService videoFilePostService;

    @Resource
    private VideoService videoService;

    @Resource
    private VideoPostMapper videoPostMapper;

    @Resource
    private RedisComponent redisComponent;

    @ApiOperation(value = "加载所有视频")
    @AuthCheck
    @GetMapping("/loadAllVideo")
    public BaseResponse<List<Video>> loadAllVideo(HttpServletRequest request) {
        String token = request.getHeader("token");
        UserVO userVO = redisComponent.getUserVO(token);
        QueryWrapper<Video> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userVO.getId())
                .orderByDesc("createTime");
        List<Video> list = videoService.list(queryWrapper);
        return ResultUtils.success(list);
    }

    @ApiOperation(value = "发布视频")
    @AuthCheck
    @PostMapping("/addPostVideo")
    public BaseResponse<String> addPostVideo(@RequestBody VideoPostAddRequest videoPostAddRequest, HttpServletRequest request) {
        String token = request.getHeader("token");
        UserVO userVO = redisComponent.getUserVO(token);
        List<VideoFilePost> videoFilePosts = videoPostAddRequest.getVideoFilePosts();
        VideoPost videoPost = new VideoPost();
        BeanUtil.copyProperties(videoPostAddRequest, videoPost);
        videoPost.setUserId(userVO.getId());
        videoPostService.addVideoPost(videoPost, videoFilePosts);
        return ResultUtils.success("投稿成功");
    }

    @ApiOperation(value = "修改视频")
    @AuthCheck
    @PostMapping("/updatePostVideo")
    public void updatePostVide(@RequestBody VideoPostUpdateRequest videoPostUpdateRequest, HttpServletRequest request) {
        String token = request.getHeader("token");
        UserVO userVO = redisComponent.getUserVO(token);
        List<VideoFilePost> videoFilePosts = videoPostUpdateRequest.getVideoFilePosts();
        VideoPost videoPost = new VideoPost();
        BeanUtil.copyProperties(videoPostUpdateRequest, videoPost);
        videoPost.setUserId(userVO.getId());
        videoPostService.updateVideoPost(videoPost, videoFilePosts);
    }

    @ApiOperation(value = "查询稿件接口")
    @AuthCheck
    @PostMapping("/loadVideoPost")
    public BaseResponse<Page<VideoPostVO>> loadVideoPost(@RequestBody VideoPostQueryRequest videoPostQueryRequest,
                                                         HttpServletRequest request) {
        String token = request.getHeader("token");
        UserVO userVO = redisComponent.getUserVO(token);
        Integer status = videoPostQueryRequest.getStatus();
        if (status != null) {
            if (status == -1) {
                Integer[] array = {VideoStatusEnum.STATUS3.getValue(), VideoStatusEnum.STATUS4.getValue()};
                List<Integer> list = new ArrayList<>(Arrays.asList(array));
                videoPostQueryRequest.setExcludeStatus(list);
            }
        }
        videoPostQueryRequest.setQueryUserInfo(false);
        List<VideoPostVO> record = videoPostMapper.loadVideoPost(videoPostQueryRequest, userVO.getId(), userVO.getUserRole().equals("admin"));
        Page<VideoPostVO> res = new Page<>();
        res.setRecords(record);
        res.setCurrent(videoPostQueryRequest.getCurrent());
        res.setSize(videoPostQueryRequest.getPageSize());
        res.setSize(record.size());
        return ResultUtils.success(res);
    }

    @ApiOperation(value = "获取各个状态视频的数量")
    @AuthCheck
    @GetMapping("/getVideoStatusCount")
    public BaseResponse<VideoStatusCountVO> getVideoStatusCount(HttpServletRequest request) {
        String token = request.getHeader("token");
        UserVO userVO = redisComponent.getUserVO(token);
        QueryWrapper<VideoPost> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userVO.getId());
        queryWrapper.eq("status", VideoStatusEnum.STATUS3.getValue());
        Integer auditPassCount = Math.toIntExact(videoPostService.count(queryWrapper));
        queryWrapper.clear();
        queryWrapper.eq("userId", userVO.getId());
        queryWrapper.eq("status", VideoStatusEnum.STATUS4.getValue());
        Integer auditFailCount = Math.toIntExact(videoPostService.count(queryWrapper));
        Integer[] status = {VideoStatusEnum.STATUS3.getValue(), VideoStatusEnum.STATUS4.getValue()};
        List<Integer> list = Arrays.asList(status);
        queryWrapper.clear();
        queryWrapper.eq("userId", userVO.getId());
        queryWrapper.notIn("status", list);
        Integer inProcessCount = Math.toIntExact(videoPostService.count(queryWrapper));
        VideoStatusCountVO videoStatusCountVO = new VideoStatusCountVO();
        videoStatusCountVO.setAuditPassCount(auditPassCount);
        videoStatusCountVO.setAuditFailCount(auditFailCount);
        videoStatusCountVO.setInProcessCount(inProcessCount);
        return ResultUtils.success(videoStatusCountVO);
    }

    @ApiOperation(value = "获取视频信息")
    @AuthCheck
    @GetMapping("/getVideoInfoById")
    public BaseResponse<VideoPostEditVO> getVideoInfoById(@NotEmpty String videoId, HttpServletRequest request) {
        String token = request.getHeader("token");
        UserVO userVO = redisComponent.getUserVO(token);
        VideoPost videoPost = videoPostService.getById(videoId);
        if (videoPost == null || !videoPost.getUserId().equals(userVO.getId())) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        QueryWrapper<VideoFilePost> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("videoId", videoId)
                .orderByAsc("fileIndex");
        List<VideoFilePost> videoFilePostList = videoFilePostService.list(queryWrapper);
        VideoPostEditVO videoPostEditVO = new VideoPostEditVO();
        videoPostEditVO.setVideoPost(videoPost);
        videoPostEditVO.setVideoFilePostList(videoFilePostList);
        return ResultUtils.success(videoPostEditVO);
    }

    @ApiOperation(value = "修改视频互动设置")
    @AuthCheck
    @PostMapping("/changeVideoInteraction")
    public BaseResponse<String> changeVideoInteraction(@NotEmpty String videoId, String interaction, HttpServletRequest request) {
        String token = request.getHeader("token");
        UserVO userVO = redisComponent.getUserVO(token);
        videoService.changeInteraction(videoId,  userVO.getId(), interaction);
        return ResultUtils.success("修改互动设置成功");
    }

    @ApiOperation(value = "删除视频")
    @AuthCheck
    @PostMapping("/deleteVideo")
    public void deleteVideo(@NotEmpty String videoId, HttpServletRequest request) {
        String token = request.getHeader("token");
        UserVO userVO = redisComponent.getUserVO(token);
        videoService.deleteVideo(videoId, userVO.getId());
    }
}
