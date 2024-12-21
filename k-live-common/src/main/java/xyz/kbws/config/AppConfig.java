package xyz.kbws.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author kbws
 * @date 2024/11/26
 * @description:
 */
@Data
@Configuration
public class AppConfig {

    @Value("${project.folder:}")
    private String projectFolder;

    @Value("${ffmpeg.showLog:}")
    private Boolean showFFmpegLog;

    @Value("${es.host:127.0.0.1:9200}")
    private String esHost;

    @Value("${es.indexName:klive_video}")
    private String esIndexName;
}
