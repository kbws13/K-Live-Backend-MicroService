package xyz.kbws.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.kbws.model.entity.Action;
import xyz.kbws.model.query.ActionQuery;

import java.util.List;

/**
 * @author fangyuan
 * @description 针对表【action(用户行为 点赞、评论)】的数据库操作Service
 * @createDate 2024-12-07 12:13:10
 */
public interface ActionService extends IService<Action> {

    void saveAction(Action action);

    List<Action> findList(ActionQuery actionQuery);
}
