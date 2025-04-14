package xyz.kbws;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.kbws.es.EsComponent;
import xyz.kbws.model.entity.Video;

import javax.annotation.Resource;
import java.util.Date;
import java.util.UUID;

/**
 * @author kbws
 * @date 2025/4/15
 * @description:
 */
@SpringBootTest
public class TestApplication {

    @Resource
    private EsComponent esComponent;

    @Test
    public void test() {
        esComponent.createIndex();
        Video video = new Video();
        video.setId(UUID.randomUUID().toString());  // 随机生成唯一 ID
        video.setCover("https://example.com/cover.jpg");
        video.setName("测试视频");
        video.setUserId("user123");
        video.setCategoryId(1);
        video.setParentCategoryId(0);
        video.setStatus(3);  // 审核成功
        video.setPostType(0);  // 自己制作
        video.setOriginInfo("原创");
        video.setTags("测试,单元测试");
        video.setIntroduction("这是一个测试用的视频简介");
        video.setInteraction("{\"danmu\":true,\"comment\":true}");
        video.setDuration(120);  // 2 分钟
        video.setPlayCount(100);
        video.setLikeCount(10);
        video.setDanmuCount(5);
        video.setCommentCount(3);
        video.setCoinCount(1);
        video.setCollectCount(2);
        video.setRecommendType(1);
        video.setLastPlayTime(new Date());
        video.setCreateTime(new Date());
        video.setUpdateTime(new Date());

        video.setNickName("测试用户");
        video.setAvatar("https://example.com/avatar.jpg");

        esComponent.saveDoc(video);
    }
}
