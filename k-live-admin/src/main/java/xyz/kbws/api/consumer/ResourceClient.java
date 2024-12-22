package xyz.kbws.api.consumer;

import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * @author kbws
 * @date 2024/12/21
 * @description:
 */
@FeignClient(name = "k-live-resource", contextId = "resourceClient")
public interface ResourceClient {

    @PostMapping(value = "/inner/file/uploadImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String uploadImage(@RequestPart MultipartFile file, @RequestParam Boolean createThumbnail);

    @GetMapping("/inner/file/getResource")
    Response getResource(@RequestParam String sourceName, HttpServletResponse response);

    @GetMapping("/inner/file/videoResource/{fileId}")
    Response videoResource(@PathVariable("fileId") String fileId);

    @GetMapping("/inner/file/videoResourceTS/{fileId}/{ts}")
    Response videoResourceTS(@PathVariable("fileId") String fileId, @PathVariable("ts") String ts);

}
