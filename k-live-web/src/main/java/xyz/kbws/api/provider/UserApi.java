package xyz.kbws.api.provider;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;
import xyz.kbws.model.dto.user.UserChangeStatusRequest;
import xyz.kbws.model.dto.user.UserLoadRequest;
import xyz.kbws.model.entity.User;
import xyz.kbws.service.UserService;

import javax.annotation.Resource;

/**
 * @author kbws
 * @date 2024/12/21
 * @description:
 */
@RestController
@RequestMapping("/inner/user")
public class UserApi {

    @Resource
    private UserService userService;

    @PostMapping("/selectById")
    public User selectById(String userId) {
        return userService.getById(userId);
    }

    @PostMapping("/updateCountInfo")
    public Integer updateCoinCount(String userId, Integer changeCount) {
        return userService.updateCoinCount(userId, changeCount);
    }

    @GetMapping("/count")
    public Integer count() {
        return Math.toIntExact(userService.count());
    }

    @PostMapping("/page")
    Page<User> page(@RequestBody UserLoadRequest userLoadRequest) {
        long current = userLoadRequest.getCurrent();
        long pageSize = userLoadRequest.getPageSize();
        userLoadRequest.setSortField("createTime");
        QueryWrapper<User> queryWrapper = userService.getQueryWrapper(userLoadRequest);
        return userService.page(new Page<>(current, pageSize), queryWrapper);
    }

    @PostMapping("/changeStatus")
    Boolean changeStatus(@RequestBody UserChangeStatusRequest userChangeStatusRequest) {
        return userService.changeStatus(userChangeStatusRequest);
    }
}
