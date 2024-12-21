package xyz.kbws.controller;

import cn.hutool.core.bean.BeanUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.kbws.annotation.AuthCheck;
import xyz.kbws.annotation.RecordMessage;
import xyz.kbws.model.dto.action.ActionDoRequest;
import xyz.kbws.model.entity.Action;
import xyz.kbws.model.enums.MessageTypeEnum;
import xyz.kbws.model.vo.UserVO;
import xyz.kbws.redis.RedisComponent;
import xyz.kbws.service.ActionService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author kbws
 * @date 2024/12/21
 * @description: 操作接口
 */
@Api(tags = "操作接口")
@RestController
@RequestMapping("/action")
public class ActionController {

    @Resource
    private ActionService actionService;

    @Resource
    private RedisComponent redisComponent;

    @ApiOperation(value = "执行行为")
    @AuthCheck
    @RecordMessage(messageType = MessageTypeEnum.LIKE)
    @PostMapping("/doAction")
    public void doAction(@RequestBody ActionDoRequest actionDoRequest, HttpServletRequest request) {
        String token = request.getHeader("token");
        UserVO userVO = redisComponent.getUserVO(token);
        Action action = new Action();
        BeanUtil.copyProperties(actionDoRequest, action);
        action.setUserId(userVO.getId());
        actionService.saveAction(action);
    }
}
