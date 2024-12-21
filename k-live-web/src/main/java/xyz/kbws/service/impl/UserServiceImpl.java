package xyz.kbws.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.kbws.common.ErrorCode;
import xyz.kbws.config.SystemSetting;
import xyz.kbws.constant.UserConstant;
import xyz.kbws.exception.BusinessException;
import xyz.kbws.mapper.FocusMapper;
import xyz.kbws.mapper.UserMapper;
import xyz.kbws.mapper.VideoMapper;
import xyz.kbws.model.dto.user.UserChangeStatusRequest;
import xyz.kbws.model.dto.user.UserLoadRequest;
import xyz.kbws.model.dto.user.UserLoginRequest;
import xyz.kbws.model.dto.user.UserRegisterRequest;
import xyz.kbws.model.entity.Focus;
import xyz.kbws.model.entity.User;
import xyz.kbws.model.enums.UserRoleEnum;
import xyz.kbws.model.enums.UserSexEnum;
import xyz.kbws.model.vo.CountInfoVO;
import xyz.kbws.model.vo.UserVO;
import xyz.kbws.redis.RedisComponent;
import xyz.kbws.service.UserService;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author fangyuan
 * @description 针对表【user(用户表)】的数据库操作Service实现
 * @createDate 2024-11-24 22:14:25
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private FocusMapper focusMapper;

    @Resource
    private VideoMapper videoMapper;

    @Resource
    private RedisComponent redisComponent;

    @Override
    public Boolean register(UserRegisterRequest userRegisterRequest) {
        if (!userRegisterRequest.getPassword().equals(userRegisterRequest.getCheckPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次密码不一致");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", userRegisterRequest.getEmail())
                .or().eq("nickName", userRegisterRequest.getNickName());
        User one = this.getOne(queryWrapper);
        if (one != null) {
            return false;
        }
        User user = new User();
        user.setId(RandomUtil.randomNumbers(UserConstant.LENGTH_10));
        user.setNickName(userRegisterRequest.getNickName());
        user.setEmail(userRegisterRequest.getEmail());
        user.setPassword(SecureUtil.md5(userRegisterRequest.getPassword()));
        user.setUserRole(UserRoleEnum.USER.getValue());
        user.setSex(UserSexEnum.SECRECY.getValue());
        user.setTheme(UserConstant.ONE);
        // 初始化用户的硬币
        SystemSetting systemSetting = redisComponent.getSystemSetting();
        user.setCurrentCoinCount(systemSetting.getRegisterCoinCount());
        user.setTotalCoinCount(systemSetting.getRegisterCoinCount());
        return this.save(user);
    }

    @Override
    public UserVO login(UserLoginRequest userLoginRequest, String ip) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", userLoginRequest.getEmail());
        User user = this.getOne(queryWrapper);
        if (user == null || !user.getPassword().equals(userLoginRequest.getPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或密码错误");
        }
        if (user.getUserRole().equals(UserRoleEnum.BAN.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "该账号已被禁用");
        }
        user.setLastLoginTime(new Date());
        user.setLastLoginIp(ip);
        this.updateById(user);
        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user, userVO);
        // 设置粉丝数、关注数、硬币数
        Integer fansCount = focusMapper.selectFansCount(user.getId());
        Integer focusCount = focusMapper.selectFocusCount(user.getId());
        userVO.setFansCount(fansCount);
        userVO.setFocusCount(focusCount);
        redisComponent.saveUserVO(userVO);
        return userVO;
    }

    @Override
    public UserVO getUserDetailInfo(String currentUserId, String userId) {
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user, userVO);
        Integer fansCount = focusMapper.selectFansCount(userId);
        Integer focusCount = focusMapper.selectFocusCount(userId);
        userVO.setFansCount(fansCount);
        userVO.setFocusCount(focusCount);
        // 获赞数、播放数
        CountInfoVO countInfoVO = videoMapper.selectSumCountInfoVO(userId);
        BeanUtil.copyProperties(countInfoVO, userVO);
        if (currentUserId != null) {
            QueryWrapper<Focus> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userId", userId)
                    .eq("focusUserId", currentUserId);
            Focus focus = focusMapper.selectOne(queryWrapper);
            userVO.setHaveFocus(focus != null);
        }
        return userVO;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean updateUserInfo(User user, UserVO tokenUserInfo) {
        User dbUser = this.getById(user.getId());
        if (!dbUser.getNickName().equals(user.getNickName()) && dbUser.getCurrentCoinCount() < UserConstant.UPDATE_NICK_NAME_COIN) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "硬币不足，无法修改昵称");
        }
        if (!dbUser.getNickName().equals(user.getNickName())) {
            int count = userMapper.updateCoinCount(user.getId(), -UserConstant.UPDATE_NICK_NAME_COIN);
            if (count == 0) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "硬币不足，无法修改昵称");
            }
        }
        boolean res = this.updateById(user);

        boolean update = false;
        if (!user.getAvatar().equals(tokenUserInfo.getAvatar())) {
            tokenUserInfo.setAvatar(user.getAvatar());
            update = true;
        }
        if (!user.getNickName().equals(tokenUserInfo.getNickName())) {
            tokenUserInfo.setNickName(user.getNickName());
            update = true;
        }
        if (update) {
            redisComponent.updateUserVO(tokenUserInfo);
        }
        return res;
    }

    @Override
    public Boolean changeStatus(UserChangeStatusRequest userChangeStatusRequest) {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", userChangeStatusRequest.getUserId())
                .set("userRole", userChangeStatusRequest.getUserRole());
        return this.update(updateWrapper);
    }

    @Override
    public QueryWrapper<User> getQueryWrapper(UserLoadRequest userLoadRequest) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        String nickName = userLoadRequest.getNickName();
        String email = userLoadRequest.getEmail();
        String sortField = userLoadRequest.getSortField();

        queryWrapper.like(StrUtil.isNotEmpty(nickName), "nickName", nickName);
        queryWrapper.like(StrUtil.isNotEmpty(email), "email", email);
        queryWrapper.orderByDesc(StrUtil.isNotEmpty(sortField), "sortField", sortField);
        return queryWrapper;
    }
}




