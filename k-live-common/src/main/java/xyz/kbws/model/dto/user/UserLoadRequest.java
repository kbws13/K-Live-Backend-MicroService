package xyz.kbws.model.dto.user;

import lombok.Data;
import xyz.kbws.common.PageRequest;

import java.io.Serializable;

/**
 * @author kbws
 * @date 2024/12/18
 * @description:
 */
@Data
public class UserLoadRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = -6204302910536859082L;
    /**
     * 昵称
     */
    private String nickName;
    /**
     * 邮箱
     */
    private String email;
}
