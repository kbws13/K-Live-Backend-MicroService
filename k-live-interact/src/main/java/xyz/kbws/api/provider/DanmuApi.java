package xyz.kbws.api.provider;

import org.springframework.web.bind.annotation.*;
import xyz.kbws.model.entity.Danmu;
import xyz.kbws.model.query.DanmuQuery;
import xyz.kbws.service.DanmuService;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author kbws
 * @date 2024/12/22
 * @description:
 */
@RestController
@RequestMapping("/inner/danmu")
public class DanmuApi {

    @Resource
    private DanmuService danmuService;

    @PostMapping("/selectListByParam")
    List<Danmu> selectListByParam(DanmuQuery danmuQuery) {
        return danmuService.selectListByParam(danmuQuery);
    }

    @PostMapping("/deleteDanmu")
    void deleteDanmu(String userId, Integer danmuId) {
        danmuService.deleteDanmu(userId, danmuId);
    }
}
