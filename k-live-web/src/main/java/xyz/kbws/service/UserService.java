package xyz.kbws.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import xyz.kbws.model.dto.user.UserChangeStatusRequest;
import xyz.kbws.model.dto.user.UserLoadRequest;
import xyz.kbws.model.dto.user.UserLoginRequest;
import xyz.kbws.model.dto.user.UserRegisterRequest;
import xyz.kbws.model.entity.User;
import xyz.kbws.model.vo.UserVO;

/**
 * @author fangyuan
 * @description 针对表【user(用户表)】的数据库操作Service
 * @createDate 2024-11-24 22:14:25
 */
public interface UserService extends IService<User> {

    Boolean register(UserRegisterRequest userRegisterRequest);

    UserVO login(UserLoginRequest userLoginRequest, String ip);

    UserVO getUserDetailInfo(String currentUserId, String userId);

    Boolean updateUserInfo(User user, UserVO tokenUserInfo);

    Boolean changeStatus(UserChangeStatusRequest userChangeStatusRequest);

    QueryWrapper<User> getQueryWrapper(UserLoadRequest userLoadRequest);

}
