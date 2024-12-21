package xyz.kbws.annotation;

import xyz.kbws.model.enums.MessageTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author kbws
 * @date 2024/12/15
 * @description: 记录消息注解
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RecordMessage {

    MessageTypeEnum messageType();
}
