package xyz.kbws.model.vo;

import lombok.Data;
import xyz.kbws.model.entity.Focus;

import java.io.Serializable;

/**
 * @author kbws
 * @date 2024/12/10
 * @description:
 */
@Data
public class FocusVO extends Focus implements Serializable {

    private static final long serialVersionUID = -5146922216277083466L;
    private String otherNickName;
    private String otherUserId;
    private String otherPersonIntroduction;
    private String otherAvatar;
    private Integer focusType;
}
