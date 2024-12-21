package xyz.kbws.model.dto.file;

import lombok.Data;

import java.io.Serializable;

/**
 * @author kbws
 * @date 2024/11/28
 * @description: 准备上传视频请求
 */
@Data
public class PreUploadVideoRequest implements Serializable {

    private static final long serialVersionUID = 7716993592392586645L;
    private String fileName;
    private Integer chunks;
}
