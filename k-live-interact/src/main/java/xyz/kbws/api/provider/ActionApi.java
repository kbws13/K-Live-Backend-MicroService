package xyz.kbws.api.provider;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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

    @PostMapping("/list")
    List<Action> list(QueryWrapper<Action> queryWrapper) {
        return actionService.list(queryWrapper);
    }
}
