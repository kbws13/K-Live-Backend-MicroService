package xyz.kbws.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.kbws.annotation.AuthCheck;
import xyz.kbws.api.consumer.VideoFileClient;
import xyz.kbws.common.BaseResponse;
import xyz.kbws.common.ErrorCode;
import xyz.kbws.common.ResultUtils;
import xyz.kbws.config.AppConfig;
import xyz.kbws.config.SystemSetting;
import xyz.kbws.constant.CommonConstant;
import xyz.kbws.constant.FileConstant;
import xyz.kbws.constant.MqConstant;
import xyz.kbws.exception.BusinessException;
import xyz.kbws.model.dto.file.PreUploadVideoRequest;
import xyz.kbws.model.dto.video.VideoPlayRequest;
import xyz.kbws.model.entity.VideoFile;
import xyz.kbws.model.vo.UploadingFileVO;
import xyz.kbws.model.vo.UserVO;
import xyz.kbws.rabbitmq.MessageProducer;
import xyz.kbws.redis.RedisComponent;
import xyz.kbws.utils.FFmpegUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author kbws
 * @date 2024/12/21
 * @description: 文件接口
 */
@Slf4j
@Api(tags = "文件接口")
@RestController
public class FileController {
    @Resource
    private VideoFileClient videoFileClient;

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private FFmpegUtil fFmpegUtil;

    @Resource
    private AppConfig appConfig;

    @Resource
    private MessageProducer messageProducer;

    @ApiOperation(value = "获取资源接口")
    @GetMapping("/getResource")
    public void getResource(@NotNull String sourceName, HttpServletResponse response) {
        if (StrUtil.isEmpty(sourceName) || !sourceName.contains(".") || !pathIsOk(sourceName)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        String suffix = sourceName.substring(sourceName.lastIndexOf("."));
        response.setContentType("image/" + suffix.replace(".", ""));
        response.setHeader("Cache-Control", "max-age=2592000");
        readFile(response, sourceName);
    }

    @ApiOperation(value = "准备上传视频接口")
    @AuthCheck
    @PostMapping("/preUploadVideo")
    public BaseResponse<String> preUploadVideo(@RequestBody PreUploadVideoRequest preUploadVideoRequest, HttpServletRequest request) {
        String token = request.getHeader("token");
        UserVO userVO = redisComponent.getUserVO(token);
        String fileName = preUploadVideoRequest.getFileName();
        Integer chunks = preUploadVideoRequest.getChunks();
        String uploadId = redisComponent.savePreVideoFile(userVO.getId(), fileName, chunks);
        return ResultUtils.success(uploadId);
    }

    @ApiOperation(value = "上传视频接口")
    @AuthCheck
    @PostMapping("/uploadVideo")
    public BaseResponse<Boolean> uploadVideo(@NotNull MultipartFile chunkFile, @NotNull Integer chunkIndex, @NotEmpty String uploadId, HttpServletRequest request) throws IOException {
        String token = request.getHeader("token");
        UserVO userVO = redisComponent.getUserVO(token);
        UploadingFileVO fileVO = redisComponent.getUploadVideoFile(userVO.getId(), uploadId);
        if (fileVO == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件不存在");
        }
        SystemSetting systemSetting = redisComponent.getSystemSetting();
        if (fileVO.getFileSize() > systemSetting.getVideoSize() * FileConstant.MB_SIZE) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "文件超过大小限制");
        }
        // 判断分片
        if ((chunkIndex - 1) > fileVO.getChunkIndex() || chunkIndex > fileVO.getChunks() - 1) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        String folder = appConfig.getProjectFolder() + FileConstant.FILE_FOLDER + FileConstant.FILE_FOLDER_TEMP + fileVO.getFilePath();
        File targetFile = new File(folder + "/" + chunkIndex);
        chunkFile.transferTo(targetFile);
        fileVO.setChunkIndex(chunkIndex);
        fileVO.setFileSize(fileVO.getFileSize() + chunkFile.getSize());
        redisComponent.updateUploadVideoFile(userVO.getId(), fileVO);
        return ResultUtils.success(true);
    }

    @ApiOperation(value = "删除已上传视频")
    @AuthCheck
    @PostMapping("/delUploadVide")
    public BaseResponse<String> delUploadVide(@NotNull String uploadId, HttpServletRequest request) {
        String token = request.getHeader("token");
        UserVO userVO = redisComponent.getUserVO(token);
        UploadingFileVO fileVO = redisComponent.getUploadVideoFile(userVO.getId(), uploadId);
        if (fileVO == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件不存在请重新上传");
        }
        redisComponent.deleteUploadVideoFile(userVO.getId(), uploadId);
        FileUtil.del(new File(appConfig.getProjectFolder() + FileConstant.FILE_FOLDER + FileConstant.FILE_FOLDER_TEMP + fileVO.getFilePath()));
        return ResultUtils.success(uploadId);
    }

    @ApiOperation(value = "上传图片")
    @AuthCheck
    @PostMapping("/uploadImage")
    public BaseResponse<String> uploadImage(@NotNull MultipartFile file, @NotNull Boolean createThumbnail) throws IOException {
        return ResultUtils.success(uploadImageInner(file, createThumbnail));
    }

    public String uploadImageInner(MultipartFile file, Boolean createThumbnail) throws IOException {
        String date = DateUtil.format(DateUtil.date(), "yyyyMMdd");
        String folder = appConfig.getProjectFolder() + FileConstant.FILE_FOLDER + FileConstant.FILE_COVER + date;
        File folderFile = new File(folder);
        if (!folderFile.exists()) {
            folderFile.mkdirs();
        }
        String fileName = file.getOriginalFilename();
        String fileSuffix = fileName.substring(fileName.lastIndexOf("."));
        String realFileName = RandomUtil.randomString(CommonConstant.LENGTH_30) + fileSuffix;
        String filePath = folder + "/" + realFileName;
        file.transferTo(new File(filePath));
        if (createThumbnail) {
            // 生成缩略图
            fFmpegUtil.createImageThumbnail(filePath);
        }
        return FileConstant.FILE_COVER + date + "/" + realFileName;
    }

    @ApiOperation(value = "获取视频 m3u8 文件")
    @GetMapping("/videoResource/{fileId}")
    public void videoResource(@PathVariable("fileId") @NotEmpty(message = "文件 id 不能为空") String fileId, HttpServletRequest request, HttpServletResponse response) {
        VideoFile videoFile = videoFileClient.getVideoFileById(fileId);
        String filePath = videoFile.getFilePath();
        readFile(response, filePath + File.separator + FileConstant.M3U8_NAME);
        // 更新视频的观看信息
        String token = request.getHeader("token");
        UserVO userVO = redisComponent.getUserVO(token);
        VideoPlayRequest videoPlayRequest = new VideoPlayRequest();
        videoPlayRequest.setVideoId(videoFile.getVideoId());
        videoPlayRequest.setFileIndex(videoFile.getFileIndex());
        if (userVO != null) {
            videoPlayRequest.setUserId(userVO.getId());
        }
        messageProducer.sendMessage(MqConstant.NEWS_QUEUE, MqConstant.NEWS_QUEUE, JSONUtil.toJsonStr(videoPlayRequest));
    }

    @ApiOperation(value = "获取视频 TS 文件")
    @GetMapping("/videoResource/{fileId}/{ts}")
    public void videoResourceTS(@PathVariable("fileId") @NotEmpty(message = "文件 id 不能为空") String fileId,
                                @PathVariable("ts") @NotEmpty(message = "ts 不能为空") String ts,
                                HttpServletResponse response) {
        VideoFile videoFile = videoFileClient.getVideoFileById(fileId);
        String filePath = videoFile.getFilePath();
        readFile(response, filePath + File.separator + ts);
    }

    protected void readFile(HttpServletResponse response, String filePath) {
        File file = new File(appConfig.getProjectFolder() + FileConstant.FILE_FOLDER + filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        try (OutputStream out = response.getOutputStream(); FileInputStream in = new FileInputStream(file)) {
            byte[] bytes = new byte[1024];
            int len = 0;
            while ((len = in.read(bytes)) != -1) {
                out.write(bytes, 0, len);
            }
            out.flush();
        } catch (Exception e) {
            log.error("读取文件异常: {}", e.getMessage());
        }
    }

    private boolean pathIsOk(String path) {
        if (StrUtil.isEmpty(path)) {
            return true;
        }
        if (path.contains("../") && path.contains("..\\")) {
            return false;
        }
        return true;
    }
}
