package xyz.kbws.api.provider;

import org.springframework.web.bind.annotation.*;
import xyz.kbws.model.entity.Action;
import xyz.kbws.service.ActionService;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author kbws
 * @date 2024/12/22
 * @description:
 */
@RestController
@RequestMapping("/inner/action")
public class ActionApi {

    @Resource
    private ActionService actionService;

    @GetMapping("/list")
    List<Action> list(@RequestParam String videoId, @RequestParam String userId, @RequestParam List<Integer> types) {
        return actionService.findListByParam(videoId, userId, types);
    }
}
