package xyz.kbws.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import xyz.kbws.annotation.AuthCheck;
import xyz.kbws.common.BaseResponse;
import xyz.kbws.common.DeleteRequest;
import xyz.kbws.common.ResultUtils;
import xyz.kbws.model.dto.message.MessageLoadRequest;
import xyz.kbws.model.entity.Message;
import xyz.kbws.model.enums.MessageReadTypeEnum;
import xyz.kbws.model.query.MessageQuery;
import xyz.kbws.model.vo.MessageCountVO;
import xyz.kbws.model.vo.UserVO;
import xyz.kbws.redis.RedisComponent;
import xyz.kbws.service.MessageService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author kbws
 * @date 2024/12/21
 * @description: 消息接口
 */
@Api(tags = "消息接口")
@RestController
@RequestMapping("/message")
public class MessageController {
    @Resource
    private MessageService messageService;

    @Resource
    private RedisComponent redisComponent;

    @ApiOperation(value = "查询未读消息数量")
    @AuthCheck
    @GetMapping("/getNoReadCount")
    public BaseResponse<Long> getNoReadCount(HttpServletRequest request) {
        String token = request.getHeader("token");
        UserVO userVO = redisComponent.getUserVO(token);
        QueryWrapper<Message> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userVO.getId())
                .eq("readType", MessageReadTypeEnum.NO_READ.getValue());
        long count = messageService.count(queryWrapper);
        return ResultUtils.success(count);
    }

    @ApiOperation(value = "分组获取未读消息数量")
    @AuthCheck
    @GetMapping("/getNoReadCountByGroup")
    public BaseResponse<List<MessageCountVO>> getNoReadCountByGroup(HttpServletRequest request) {
        String token = request.getHeader("token");
        UserVO userVO = redisComponent.getUserVO(token);
        List<MessageCountVO> messageTypeNoReadCount = messageService.getMessageTypeNoReadCount(userVO.getId());
        return ResultUtils.success(messageTypeNoReadCount);
    }

    @ApiOperation(value = "一键已读")
    @AuthCheck
    @PostMapping("/readAll")
    public void readAll(@NotNull Integer messageType, HttpServletRequest request) {
        String token = request.getHeader("token");
        UserVO userVO = redisComponent.getUserVO(token);
        UpdateWrapper<Message> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("userId", userVO.getId())
                .eq("type", messageType)
                .set("readType", MessageReadTypeEnum.READ.getValue());
        messageService.update(updateWrapper);
    }

    @ApiOperation(value = "获取指定类型的消息")
    @AuthCheck
    @PostMapping("/load")
    public BaseResponse<Page<Message>> loadMessage(@RequestBody MessageLoadRequest messageLoadRequest, HttpServletRequest request) {
        String token = request.getHeader("token");
        UserVO userVO = redisComponent.getUserVO(token);
        long current = messageLoadRequest.getCurrent();
        long pageSize = messageLoadRequest.getPageSize();
        MessageQuery messageQuery = new MessageQuery();
        BeanUtil.copyProperties(messageLoadRequest, messageQuery);
        messageQuery.setUserId(userVO.getId());
        List<Message> record = messageService.selectList(messageQuery);
        Page<Message> page = new Page<>(current, pageSize);
        page.setRecords(record);
        page.setTotal(record.size());
        return ResultUtils.success(page);
    }

    @ApiOperation(value = "删除消息")
    @AuthCheck
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteMessage(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        String token = request.getHeader("token");
        UserVO userVO = redisComponent.getUserVO(token);
        QueryWrapper<Message> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userVO.getId())
                .eq("id", deleteRequest.getId());
        boolean remove = messageService.remove(queryWrapper);
        return ResultUtils.success(remove);
    }
}
