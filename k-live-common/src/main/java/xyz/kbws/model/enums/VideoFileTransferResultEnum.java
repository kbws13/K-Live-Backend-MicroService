package xyz.kbws.model.enums;

import lombok.Getter;

/**
 * @author kbws
 * @date 2024/11/30
 * @description: 视频文件转码结果枚举
 */
@Getter
public enum VideoFileTransferResultEnum {
    TRANSFER("转码中", 0),
    SUCCESS("转码成功", 1),
    FAIL("转码失败", 2),
    ;

    private final String text;

    private final Integer value;

    VideoFileTransferResultEnum(String text, Integer value) {
        this.text = text;
        this.value = value;
    }
}
