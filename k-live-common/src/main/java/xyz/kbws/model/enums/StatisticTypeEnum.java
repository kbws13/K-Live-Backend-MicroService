package xyz.kbws.model.enums;

import lombok.Getter;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author kbws
 * @date 2024/12/8
 * @description: 数据统计类型枚举
 */
@Getter
public enum StatisticTypeEnum {

    PLAY("播放量", 0),
    FANS("粉丝", 1),
    LIKE("点赞", 2),
    COLLECTION("收藏", 3),
    COIN("投币", 4),
    COMMENT("评论", 5),
    DANMU("弹幕", 6),
    ;

    private final String text;
    private final Integer value;

    StatisticTypeEnum(String text, Integer value) {
        this.text = text;
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
    public static StatisticTypeEnum getEnumByValue(Integer value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (StatisticTypeEnum anEnum : StatisticTypeEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }
}
