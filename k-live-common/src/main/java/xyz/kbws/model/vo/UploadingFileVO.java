package xyz.kbws.model.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

/**
 * @author kbws
 * @date 2024/11/28
 * @description: 上传文件的信息
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UploadingFileVO implements Serializable {

    private static final long serialVersionUID = 4117680417301377511L;
    private String uploadId;
    private String fileName;
    private Integer chunkIndex;
    private Integer chunks;
    private Long fileSize = 0L;
    private String filePath;
}
