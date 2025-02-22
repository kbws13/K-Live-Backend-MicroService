package xyz.kbws.controller;

import feign.Response;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.kbws.api.consumer.ResourceClient;
import xyz.kbws.common.BaseResponse;
import xyz.kbws.common.ResultUtils;
import xyz.kbws.utils.ResponseUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

/**
 * @author kbws
 * @date 2024/12/22
 * @description: 文件接口
 */
@Slf4j
@Api(tags = "文件接口")
@RestController
@RequestMapping("/file")
public class FileController {

    @Resource
    private ResourceClient resourceClient;

    @PostMapping("/uploadImage")
    public BaseResponse<String> uploadImage(@NotNull MultipartFile file, @NotNull Boolean createThumbnail) {
        String res = resourceClient.uploadImage(file, createThumbnail);
        return ResultUtils.success(res);
    }

    @GetMapping("/getResource")
    public void getResource(@NotNull String sourceName, HttpServletResponse response) {
        Response resource = resourceClient.getResource(sourceName, response);
        ResponseUtil.coverFileResponse2Stream(response, resource);
    }

    @GetMapping("/videoResource/{fileId}")
    public void videoResource(@PathVariable("fileId") String fileId, HttpServletResponse response) {
        Response resource = resourceClient.videoResource(fileId);
        ResponseUtil.coverFileResponse2Stream(response, resource);
    }

    @GetMapping("/videoResource/{fileId}/{ts}")
    public void videoResourceTS(@PathVariable("fileId") String fileId, @PathVariable("ts") String ts, HttpServletResponse response) {
        Response resource = resourceClient.videoResourceTS(fileId, ts);
        ResponseUtil.coverFileResponse2Stream(response, resource);
    }
}
