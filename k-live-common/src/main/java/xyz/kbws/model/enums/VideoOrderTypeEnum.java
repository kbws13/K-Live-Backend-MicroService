package xyz.kbws.model.enums;

import lombok.Getter;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author kbws
 * @date 2024/11/24
 * @description: 用户行为枚举
 */
@Getter
public enum VideoOrderTypeEnum {
    CREATE_TIME("最新发布", "createTime", 0),
    PLAY_COUNT("最多播放", "playCount", 1),
    COLLECT_COUNT("最多收藏", "collectCount", 2),
    ;

    private final String text;
    private final String field;
    private final Integer value;

    VideoOrderTypeEnum(String text, String field, Integer value) {
        this.text = text;
        this.field = field;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<Integer> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static VideoOrderTypeEnum getEnumByValue(Integer value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (VideoOrderTypeEnum anEnum : VideoOrderTypeEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }
}
