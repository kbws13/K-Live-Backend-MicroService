package xyz.kbws.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.kbws.api.consumer.UserClient;
import xyz.kbws.api.consumer.VideoClient;
import xyz.kbws.common.ErrorCode;
import xyz.kbws.constant.UserConstant;
import xyz.kbws.exception.BusinessException;
import xyz.kbws.mapper.ActionMapper;
import xyz.kbws.mapper.VideoCommentMapper;
import xyz.kbws.model.entity.Action;
import xyz.kbws.model.entity.Video;
import xyz.kbws.model.enums.SearchOrderTypeEnum;
import xyz.kbws.model.enums.UserActionTypeEnum;
import xyz.kbws.model.query.ActionQuery;
import xyz.kbws.service.ActionService;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * @author fangyuan
 * @description 针对表【action(用户行为 点赞、评论)】的数据库操作Service实现
 * @createDate 2024-12-07 12:13:10
 */
@Service
public class ActionServiceImpl extends ServiceImpl<ActionMapper, Action>
        implements ActionService {

    @Resource
    private VideoClient videoClient;

    @Resource
    private UserClient userClient;

    @Resource
    private VideoCommentMapper videoCommentMapper;

    @Resource
    private ActionMapper actionMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveAction(Action action) {
        Video video = videoClient.selectById(action.getVideoId());
        if (video == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        action.setVideoUserId(video.getUserId());
        UserActionTypeEnum actionTypeEnum = UserActionTypeEnum.getEnumByValue(action.getActionType());
        if (actionTypeEnum == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        QueryWrapper<Action> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("videoId", action.getVideoId())
                .eq(action.getCommentId() != null, "commentId", action.getCommentId())
                .eq("actionType", action.getActionType())
                .eq("userId", action.getUserId());
        Action dbAction = this.getOne(queryWrapper);
        action.setActionTime(DateUtil.date());
        int changeCount;
        switch (actionTypeEnum) {
            case VIDEO_LIKE:
            case VIDEO_COLLECT:
                if (dbAction != null) {
                    this.removeById(dbAction.getId());
                } else {
                    this.save(action);
                }
                changeCount = dbAction == null ? UserConstant.ONE : -UserConstant.ONE;
                videoClient.updateCountInfo(action.getVideoId(), actionTypeEnum.getField(), changeCount);
                if (actionTypeEnum == UserActionTypeEnum.VIDEO_LIKE) {
                    // 更新 ES 的收藏数量
                    videoClient.updateDocCount(video.getId(), SearchOrderTypeEnum.VIDEO_COLLECT, changeCount);
                }
                break;
            case VIDEO_COIN:
                if (action.getCount() != 1 && action.getCount() != 2) {
                    throw new BusinessException(ErrorCode.OPERATION_ERROR);
                }
                if (video.getUserId().equals(action.getUserId())) {
                    throw new BusinessException(ErrorCode.OPERATION_ERROR, "不能给自己投币");
                }
                if (dbAction != null) {
                    throw new BusinessException(ErrorCode.OPERATION_ERROR, "不能重复投币");
                }
                // 减少自己的硬币
                int updateCount = userClient.updateCoinCount(action.getUserId(), -action.getCount());
                if (updateCount <= 0) {
                    throw new BusinessException(ErrorCode.OPERATION_ERROR, "硬币数量不足");
                }
                // 给 UP 主增加硬币
                updateCount = userClient.updateCoinCount(action.getUserId(), action.getCount());
                if (updateCount == 0) {
                    throw new BusinessException(ErrorCode.OPERATION_ERROR, "投币失败");
                }
                this.save(action);
                videoClient.updateCountInfo(action.getVideoId(), actionTypeEnum.getField(), action.getCount());
                break;
            case COMMENT_LIKE:
            case COMMENT_HATE:
                UserActionTypeEnum opposeTypeEnum = actionTypeEnum == UserActionTypeEnum.COMMENT_LIKE ? UserActionTypeEnum.COMMENT_HATE : UserActionTypeEnum.COMMENT_LIKE;
                queryWrapper.clear();
                queryWrapper.eq("videoId", action.getVideoId())
                        .eq(action.getCommentId() != null, "commentId", action.getCommentId())
                        .eq("actionType", opposeTypeEnum.getValue())
                        .eq("userId", action.getUserId());
                Action opposeAction = this.getOne(queryWrapper);
                if (opposeAction != null) {
                    this.removeById(opposeAction.getId());
                }
                if (dbAction != null) {
                    this.removeById(dbAction.getId());
                } else {
                    this.save(action);
                }
                changeCount = dbAction == null ? UserConstant.ONE : -UserConstant.ONE;
                int opposeChangeCount = changeCount * -1;
                videoCommentMapper.updateCount(action.getCommentId(), action.getUserId(), actionTypeEnum.getField(), changeCount,
                        opposeAction == null ? null : opposeTypeEnum.getField(), opposeChangeCount);
                break;
        }
    }

    @Override
    public List<Action> findList(ActionQuery actionQuery) {
        return actionMapper.findList(actionQuery);
    }

    @Override
    public List<Action> findListByParam(String videoId, String userId, List<Integer> types) {
        return actionMapper.findListByParam(videoId, userId, types);
    }
}




