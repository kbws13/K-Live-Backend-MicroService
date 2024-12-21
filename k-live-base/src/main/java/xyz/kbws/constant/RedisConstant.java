package xyz.kbws.constant;

/**
 * @author kbws
 * @date 2024/11/24
 * @description: Redis 相关常量
 */
public interface RedisConstant {

    String CHECK_CODE = "klive:checkcode:";

    Integer TIME_1SED = 1000;

    Integer TIME_1MIN = 60 * 1000;

    Integer TIME_1DAY = 24 * 60 * 60 * 1000;

    String TOKEN_WEB = "klive:web:";

    String CATEGORY_LIST = "klive:category:list:";

    String UPLOADING_FILE = "klive:uploading:";

    String SYSTEM_SETTING = "klive:systemSetting:";

    // 视频在线
    String VIDEO_PLAY_ONLINE_COUNT_SUFFIX = "klive:video:play:online:";
    String VIDEO_PLAY_ONLINE_COUNT = VIDEO_PLAY_ONLINE_COUNT_SUFFIX + "count:%s";
    String VIDEO_PLAY_ONLINE_COUNT_USER_SUFFIX = "user:";
    String VIDEO_PLAY_ONLINE_COUNT_USER = VIDEO_PLAY_ONLINE_COUNT_SUFFIX + VIDEO_PLAY_ONLINE_COUNT_USER_SUFFIX + "%s:%s";

    String VIDEO_PLAY_COUNT = "klive:video:playCount:";

    // 热词
    String VIDEO_SEARCH_COUNT = "klive:search:";
}
