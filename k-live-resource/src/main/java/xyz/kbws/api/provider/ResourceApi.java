package xyz.kbws.api.provider;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.kbws.controller.FileController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @author kbws
 * @date 2024/12/22
 * @description:
 */
@RestController
@RequestMapping("/inner/file")
public class ResourceApi {

    @Resource
    private FileController fileController;

    @PostMapping(value = "/uploadImage")
    public String uploadImage(MultipartFile file, Boolean createThumbnail) throws IOException {
        return fileController.uploadImageInner(file, createThumbnail);
    }

    @GetMapping("/getResource")
    public void getResource(String sourceName, HttpServletResponse response) {
        fileController.getResource(sourceName, response);
    }

    @GetMapping("/videoResource/{fileId}")
    public void videoResource(@PathVariable("fileId") String fileId, HttpServletResponse response) {
        fileController.videoResource(fileId, response);
    }

    @GetMapping("/videoResourceTS/{fileId}/{ts}")
    public void videoResourceTS(@PathVariable("fileId") String fileId, @PathVariable("ts") String ts, HttpServletResponse response) {
        fileController.videoResourceTS(fileId, ts, response);
    }
}
