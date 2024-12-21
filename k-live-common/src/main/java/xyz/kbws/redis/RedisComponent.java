package xyz.kbws.redis;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.stereotype.Component;
import xyz.kbws.config.AppConfig;
import xyz.kbws.config.SystemSetting;
import xyz.kbws.constant.CommonConstant;
import xyz.kbws.constant.FileConstant;
import xyz.kbws.constant.RedisConstant;
import xyz.kbws.model.entity.Category;
import xyz.kbws.model.vo.UploadingFileVO;
import xyz.kbws.model.vo.UserVO;
import xyz.kbws.utils.JwtUtil;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author kbws
 * @date 2024/11/24
 * @description:
 */
@Component
public class RedisComponent {

    @Resource
    private RedisUtils<Object> redisUtils;

    @Resource
    private AppConfig appConfig;

    public String saveCheckCode(String code) {
        String checkCodeKey = RandomUtil.randomString(8);
        redisUtils.setEx(RedisConstant.CHECK_CODE + checkCodeKey, code, RedisConstant.TIME_1MIN);
        return checkCodeKey;
    }

    public String getCheckCode(String checkCodeKey) {
        return (String) redisUtils.get(RedisConstant.CHECK_CODE + checkCodeKey);
    }

    public void cleanCheckCode(String checkCodeKey) {
        redisUtils.delete(RedisConstant.CHECK_CODE + checkCodeKey);
    }

    public void saveUserVO(UserVO userVO) {
        String token = JwtUtil.createToken(userVO.getId(), userVO.getUserRole());
        long expireTime = System.currentTimeMillis() + RedisConstant.TIME_1DAY * 7L;
        userVO.setExpireAt(expireTime);
        userVO.setToken(token);
        redisUtils.setEx(RedisConstant.TOKEN_WEB + userVO.getId(), userVO, expireTime);
    }

    public void updateUserVO(UserVO userVO) {
        long expireTime = System.currentTimeMillis() + RedisConstant.TIME_1DAY * 7L;
        redisUtils.setEx(RedisConstant.TOKEN_WEB + userVO.getId(), userVO, expireTime);
    }

    public UserVO getUserVO(String token) {
        if (StrUtil.isEmpty(token)) {
            return null;
        }
        String userId = JwtUtil.getUserId(token);
        return (UserVO) redisUtils.get(RedisConstant.TOKEN_WEB + userId);
    }

    public void cleanToken(String token) {
        String userId = JwtUtil.getUserId(token);
        redisUtils.delete(RedisConstant.TOKEN_WEB + userId);
    }

    public void saveCategoryList(List<Category> categoryList) {
        redisUtils.set(RedisConstant.CATEGORY_LIST, categoryList);
    }

    public List<Category> getCategoryList() {
        return (List<Category>) redisUtils.get(RedisConstant.CATEGORY_LIST);
    }

    public String savePreVideoFile(String userId, String fileName, Integer chunks) {
        String uploadId = RandomUtil.randomString(CommonConstant.LENGTH_15);
        String date = DateUtil.format(DateUtil.date(), "yyyyMMdd");
        String filePath = date + "/" + userId + uploadId;
        String folder = appConfig.getProjectFolder() + FileConstant.FILE_FOLDER + FileConstant.FILE_FOLDER_TEMP + filePath;
        File folderFile = new File(folder);
        if (!folderFile.exists()) {
            folderFile.mkdirs();
        }
        UploadingFileVO uploadingFileVO = new UploadingFileVO();
        uploadingFileVO.setUploadId(uploadId);
        uploadingFileVO.setFileName(fileName);
        uploadingFileVO.setChunkIndex(0);
        uploadingFileVO.setChunks(chunks);
        uploadingFileVO.setFilePath(filePath);
        redisUtils.setEx(RedisConstant.UPLOADING_FILE + userId + uploadId, uploadingFileVO, RedisConstant.TIME_1DAY);
        return uploadId;
    }

    public UploadingFileVO getUploadVideoFile(String userId, String uploadId) {
        return (UploadingFileVO) redisUtils.get(RedisConstant.UPLOADING_FILE + userId + uploadId);
    }

    public void updateUploadVideoFile(String userId, UploadingFileVO uploadingFileVO) {
        redisUtils.setEx(RedisConstant.UPLOADING_FILE + userId + uploadingFileVO.getUploadId(), uploadingFileVO, RedisConstant.TIME_1DAY);
    }

    public void deleteUploadVideoFile(String userId, String uploadId) {
        redisUtils.delete(RedisConstant.UPLOADING_FILE + userId + uploadId);
    }

    public SystemSetting getSystemSetting() {
        SystemSetting systemSetting = (SystemSetting) redisUtils.get(RedisConstant.SYSTEM_SETTING);
        if (systemSetting == null) {
            systemSetting = new SystemSetting();
        }
        return systemSetting;
    }

    public void saveSystemSetting(SystemSetting systemSetting) {
        redisUtils.set(RedisConstant.SYSTEM_SETTING, systemSetting);
    }

    public Integer reportVideoPlayOnline(String fileId, String deviceId) {
        String userPlayOnlineKey = String.format(RedisConstant.VIDEO_PLAY_ONLINE_COUNT_USER, fileId, deviceId);
        String playOnlineKey = String.format(RedisConstant.VIDEO_PLAY_ONLINE_COUNT, fileId);
        if (!redisUtils.keyExists(userPlayOnlineKey)) {
            redisUtils.setEx(userPlayOnlineKey, fileId, RedisConstant.TIME_1SED * 8);
            return redisUtils.incrementEx(playOnlineKey, RedisConstant.TIME_1SED * 10).intValue();
        }
        redisUtils.expire(playOnlineKey, RedisConstant.TIME_1SED * 10);
        redisUtils.expire(userPlayOnlineKey, RedisConstant.TIME_1SED * 8);
        Integer count = (Integer) redisUtils.get(playOnlineKey);
        return count == null ? 1 : count;
    }

    public void decrementPlayOnlineCount(String key) {
        redisUtils.decrement(key);
    }

    public void addKeywordCount(String keyword) {
        redisUtils.zAddCount(RedisConstant.VIDEO_SEARCH_COUNT, keyword);
    }

    public List<String> getKeywordTop(Integer top) {
        List<String> list = redisUtils.getZSetList(RedisConstant.VIDEO_SEARCH_COUNT, top - 1)
                .stream()
                .map(Object::toString)
                .collect(Collectors.toList());
        return list;
    }

    public void recordVideoPlayCount(String videoId) {
        String date = DateUtil.format(DateUtil.date(), "yyyyMMdd");
        redisUtils.incrementEx(RedisConstant.VIDEO_PLAY_COUNT + date + ":" + videoId, RedisConstant.TIME_1DAY * 2L);
    }

    public Map<String, Integer> getVideoPlayCount(String date) {
        Map<String, Object> videoPlayMap = redisUtils.getBatch(RedisConstant.VIDEO_PLAY_COUNT + date);
        // 使用 stream 进行类型转换
        return videoPlayMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey, // 保留 Key
                        entry -> ((Number) entry.getValue()).intValue() // 将 Object 转成 Integer
                ));
    }

}
