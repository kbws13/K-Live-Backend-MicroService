package xyz.kbws.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author kbws
 * @date 2024/12/15
 * @description: 未读消息封装类
 */
@Data
public class MessageCountVO implements Serializable {

    private static final long serialVersionUID = 8605130088232659779L;
    private Integer messageType;
    private Integer messageCount;
}
