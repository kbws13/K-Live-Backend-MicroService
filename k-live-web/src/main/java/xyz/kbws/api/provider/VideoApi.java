package xyz.kbws.api.provider;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.kbws.es.EsComponent;
import xyz.kbws.model.entity.Video;
import xyz.kbws.model.enums.SearchOrderTypeEnum;
import xyz.kbws.service.VideoService;

import javax.annotation.Resource;

/**
 * @author kbws
 * @date 2024/12/21
 * @description:
 */
@RestController
@RequestMapping("/inner/video")
public class VideoApi {

    @Resource
    private VideoService videoService;

    @Resource
    private EsComponent esComponent;

    @PostMapping("/selectById")
    public Video selectById(String videoId) {
        return videoService.getById(videoId);
    }

    @PostMapping("/updateCountInfo")
    public void updateCountInfo(String videoId, String field, Integer changeCount) {
        videoService.updateCountInfo(videoId, field, changeCount);
    }

    @PostMapping("/updateDocCount")
    public void updateDocCount(String videoId, SearchOrderTypeEnum searchOrderTypeEnum, Integer changeCount){
        esComponent.updateDocCount(videoId, searchOrderTypeEnum.getField(), changeCount);
    }
}
