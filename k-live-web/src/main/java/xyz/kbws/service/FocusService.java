package xyz.kbws.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.kbws.model.entity.Focus;

/**
 * @author fangyuan
 * @description 针对表【focus(关注表)】的数据库操作Service
 * @createDate 2024-12-09 20:54:18
 */
public interface FocusService extends IService<Focus> {

    Boolean focusUser(String userId, String focusUserId);

    Boolean cancelFocusUser(String userId, String focusUserId);
}
