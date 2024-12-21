package xyz.kbws.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.kbws.common.ErrorCode;
import xyz.kbws.exception.BusinessException;
import xyz.kbws.mapper.FocusMapper;
import xyz.kbws.mapper.UserMapper;
import xyz.kbws.model.entity.Focus;
import xyz.kbws.model.entity.User;
import xyz.kbws.service.FocusService;

import javax.annotation.Resource;

/**
 * @author fangyuan
 * @description 针对表【focus(关注表)】的数据库操作Service实现
 * @createDate 2024-12-09 20:54:18
 */
@Service
public class FocusServiceImpl extends ServiceImpl<FocusMapper, Focus>
        implements FocusService {

    @Resource
    private UserMapper userMapper;

    @Override
    public Boolean focusUser(String userId, String focusUserId) {
        if (userId.equals(focusUserId)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "不能对自己进行此操作");
        }
        QueryWrapper<Focus> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId)
                .eq("focusUserId", focusUserId);
        Focus focus = this.getOne(queryWrapper);
        if (focus != null) {
            return true;
        }
        User user = userMapper.selectById(focusUserId);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        Focus one = new Focus();
        one.setUserId(userId);
        one.setFocusUserId(focusUserId);
        one.setFocusTime(DateUtil.date());
        return this.save(one);
    }

    @Override
    public Boolean cancelFocusUser(String userId, String focusUserId) {
        QueryWrapper<Focus> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId)
                .eq("focusUserId", focusUserId);
        return this.remove(queryWrapper);
    }
}




