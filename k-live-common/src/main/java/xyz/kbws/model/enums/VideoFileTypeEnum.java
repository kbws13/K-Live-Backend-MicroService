package xyz.kbws.model.enums;

import lombok.Getter;

/**
 * @author kbws
 * @date 2024/11/30
 * @description: 发布视频的状态枚举
 */
@Getter
public enum VideoFileTypeEnum {
    NO_UPDATE("无更新", 0),
    UPDATE("有更新", 1);

    private final String text;

    private final Integer value;

    VideoFileTypeEnum(String text, Integer value) {
        this.text = text;
        this.value = value;
    }
}
