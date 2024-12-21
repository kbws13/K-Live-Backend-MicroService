package xyz.kbws.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import xyz.kbws.annotation.AuthCheck;
import xyz.kbws.annotation.RecordMessage;
import xyz.kbws.common.BaseResponse;
import xyz.kbws.common.ResultUtils;
import xyz.kbws.constant.CommonConstant;
import xyz.kbws.model.dto.action.ActionDoRequest;
import xyz.kbws.model.entity.Action;
import xyz.kbws.model.enums.MessageTypeEnum;
import xyz.kbws.model.query.ActionQuery;
import xyz.kbws.model.vo.UserVO;
import xyz.kbws.redis.RedisComponent;
import xyz.kbws.service.ActionService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotEmpty;
import java.util.List;

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

    @ApiOperation(value = "获取收藏视频")
    @GetMapping("/loadUserCollection")
    public BaseResponse<Page<Action>> loadUserCollection(@NotEmpty String userId, Integer pageNo, Integer pageSize) {
        ActionQuery actionQuery = new ActionQuery();
        actionQuery.setUserId(userId);
        actionQuery.setQueryVideo(true);
        actionQuery.setCurrent(pageNo);
        actionQuery.setPageSize(pageSize);
        actionQuery.setSortField("actionTime");
        actionQuery.setSortOrder(CommonConstant.SORT_ORDER_DESC);
        List<Action> record = actionService.findList(actionQuery);
        Page<Action> page = new Page<>();
        page.setSize(pageSize);
        page.setTotal(record.size());
        page.setCurrent(pageNo);
        page.setRecords(record);
        return ResultUtils.success(page);
    }
}
