package xyz.kbws.api.provider;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
}
