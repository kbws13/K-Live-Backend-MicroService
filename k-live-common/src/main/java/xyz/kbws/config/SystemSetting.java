package xyz.kbws.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

/**
 * @author kbws
 * @date 2024/11/28
 * @description: 系统相关设置
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SystemSetting implements Serializable {

    private static final long serialVersionUID = 2083861547160710661L;
    private Integer registerCoinCount = 10;
    private Integer postVideoCoinCount = 5;
    private Integer videoSize = 20;
    private Integer videoPCount = 10;
    private Integer videoCount = 10;
    private Integer commentCount = 20;
    private Integer danmuCount = 20;
}
