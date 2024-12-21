package xyz.kbws.model.dto.message;

import lombok.Data;

/**
 * @author kbws
 * @date 2024/12/15
 * @description:
 */
@Data
public class MessageExtendDTO {

    private String messageContent;

    private String messageContentReply;

    /**
     * 审核状态
     */
    private Integer auditStatus;
}
