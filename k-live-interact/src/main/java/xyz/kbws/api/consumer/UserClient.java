package xyz.kbws.api.consumer;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import xyz.kbws.model.entity.User;

/**
 * @author kbws
 * @date 2024/12/21
 * @description:
 */
@FeignClient(name = "k-live-web", contextId = "userClient")
public interface UserClient {

    @PostMapping("/inner/user/selectById")
    User selectById(@RequestParam String userId);

    @PostMapping("/inner/user/updateCountInfo")
    Integer updateCoinCount(@RequestParam String userId, @RequestParam Integer changeCount);
}
