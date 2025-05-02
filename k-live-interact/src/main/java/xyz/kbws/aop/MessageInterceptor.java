package xyz.kbws.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import xyz.kbws.annotation.RecordMessage;
import xyz.kbws.common.BaseResponse;
import xyz.kbws.model.dto.action.ActionDoRequest;
import xyz.kbws.model.dto.comment.CommentAddRequest;
import xyz.kbws.model.dto.videoPost.VideoPostAuditRequest;
import xyz.kbws.model.enums.MessageTypeEnum;
import xyz.kbws.model.enums.UserActionTypeEnum;
import xyz.kbws.model.vo.UserVO;
import xyz.kbws.redis.RedisComponent;
import xyz.kbws.service.MessageService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Objects;

/**
 * @author kbws
 * @date 2024/12/15
 * @description: 记录消息切面
 */
@Slf4j
@Aspect
@Component
public class MessageInterceptor {

    @Resource
    private MessageService messageService;

    @Resource
    private RedisComponent redisComponent;

    @Around("@annotation(xyz.kbws.annotation.RecordMessage)")
    public BaseResponse doInterceptor(ProceedingJoinPoint point) throws Throwable {
        BaseResponse baseResponse = (BaseResponse) point.proceed();
        Method method = ((MethodSignature) point.getSignature()).getMethod();
        RecordMessage recordMessage = method.getAnnotation(RecordMessage.class);
        if (recordMessage != null) {
            saveMessage(recordMessage, point.getArgs(), method.getParameters());
        }
        return baseResponse;
    }

    private void saveMessage(RecordMessage recordMessage, Object[] arguments, Parameter[] parameters) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String token = request.getHeader("token");
        UserVO userVO = redisComponent.getUserVO(token);
        MessageTypeEnum messageTypeEnum = recordMessage.messageType();

        switch (messageTypeEnum) {
            case LIKE:
                ActionDoRequest actionDoRequest = (ActionDoRequest) arguments[0];
                if (Objects.equals(UserActionTypeEnum.VIDEO_COLLECT.getValue(), actionDoRequest.getActionType())) {
                    messageTypeEnum = MessageTypeEnum.COLLECTION;
                }
                messageService.saveMessage(actionDoRequest.getVideoId(), userVO == null ? null : userVO.getId(),
                        messageTypeEnum, null, null);
                break;
            case COMMENT:
                CommentAddRequest commentAddRequest = (CommentAddRequest) arguments[0];
                messageService.saveMessage(commentAddRequest.getVideoId(), userVO == null ? null : userVO.getId(),
                        messageTypeEnum, commentAddRequest.getContent(), commentAddRequest.getReplyCommentId());
                break;
            case SYSTEM:
                VideoPostAuditRequest videoPostAuditRequest = (VideoPostAuditRequest) arguments[0];
                messageService.saveMessage(videoPostAuditRequest.getVideoId(), userVO == null ? null : userVO.getId(),
                        messageTypeEnum, videoPostAuditRequest.getReason(), null);
                break;
        }
    }
}
