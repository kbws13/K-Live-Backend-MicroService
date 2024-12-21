package xyz.kbws.model.dto.message;

import lombok.Data;
import xyz.kbws.common.PageRequest;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author kbws
 * @date 2024/12/16
 * @description:
 */
@Data
public class MessageLoadRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = 4176301813403470148L;
    @NotNull(message = "消息类型不能为空")
    private Integer messageType;
}
