package xyz.kbws.model.enums;

import lombok.Getter;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author kbws
 * @date 2024/11/29
 * @description: 视频状态枚举
 */
@Getter
public enum VideoStatusEnum {
    STATUS0("转码中", 0),
    STATUS1("转码失败", 1),
    STATUS2("待审核", 2),
    STATUS3("审核通过", 3),
    STATUS4("审核不通过", 4),
    ;

    private final String text;

    private final Integer value;

    VideoStatusEnum(String text, Integer value) {
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
    public static VideoStatusEnum getEnumByValue(Integer value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (VideoStatusEnum anEnum : VideoStatusEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }
}
