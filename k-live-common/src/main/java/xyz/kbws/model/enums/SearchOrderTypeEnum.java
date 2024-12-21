package xyz.kbws.model.enums;

import lombok.Getter;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author kbws
 * @date 2024/11/24
 * @description: 视频排序枚举
 */
@Getter
public enum SearchOrderTypeEnum {
    VIDEO_PLAY("视频播放数", "playCount", 0),
    VIDEO_TIME("视频时间", "createTime", 1),
    VIDEO_DANMU("弹幕数", "danmuCount", 2),
    VIDEO_COLLECT("视频收藏数", "collectCount", 3),
    ;

    private final String text;
    private final String field;
    private final Integer value;

    SearchOrderTypeEnum(String text, String field, Integer value) {
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
    public static SearchOrderTypeEnum getEnumByValue(Integer value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (SearchOrderTypeEnum anEnum : SearchOrderTypeEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }
}
