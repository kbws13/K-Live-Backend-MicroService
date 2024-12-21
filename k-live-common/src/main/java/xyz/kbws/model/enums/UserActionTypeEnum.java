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
public enum UserActionTypeEnum {
    COMMENT_LIKE("评论点赞", "likeCount", 0),
    COMMENT_HATE("评论讨厌", "hateCount", 1),
    VIDEO_LIKE("视频点赞", "likeCount", 2),
    VIDEO_COLLECT("视频收藏", "collectCount", 3),
    VIDEO_COIN("视频投币", "coinCount", 4),
    VIDEO_COMMENT("视频评论数", "commentCount", 5),
    VIDEO_DANMU("视频弹幕数量", "danmuCount", 6),
    VIDEO_PLAY("视频播放", "playCount", 7),
    ;

    private final String text;
    private final String field;
    private final Integer value;

    UserActionTypeEnum(String text, String field, Integer value) {
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
    public static UserActionTypeEnum getEnumByValue(Integer value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (UserActionTypeEnum anEnum : UserActionTypeEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }
}
