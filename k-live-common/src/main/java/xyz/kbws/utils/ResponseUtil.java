package xyz.kbws.utils;

import feign.Response;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author kbws
 * @date 2024/12/22
 * @description: 将 FileResponse 转成 Stream 流工具类
 */
@Slf4j
public class ResponseUtil {
    public static void coverFileResponse2Stream(HttpServletResponse servletResponse, Response response) {
        Response.Body body = response.body();
        try (InputStream fileInputStream = body.asInputStream();
             OutputStream outputStream = servletResponse.getOutputStream()) {
            byte[] bytes = new byte[1024];
            int len;
            while ((len = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
            }
        } catch (Exception e) {
            log.error("FileResponse 转成 Stream 流失败");
        }
    }
}
