package xyz.kbws.api.provider;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
    List<Danmu> selectListByParam(@RequestBody DanmuQuery danmuQuery) {
        return danmuService.selectListByParam(danmuQuery);
    }

    @PostMapping("/deleteDanmu")
    void deleteDanmu(String userId, Integer danmuId) {
        danmuService.deleteDanmu(userId, danmuId);
    }

    @PostMapping("/deleteVideoDanmu")
    void deleteVideoDanmu(QueryWrapper<Danmu> danmuQueryWrapper) {
        danmuService.remove(danmuQueryWrapper);
    }
}
