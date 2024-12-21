package xyz.kbws.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @author kbws
 * @date 2024/12/18
 * @description:
 */
@Data
public class UserChangeStatusRequest implements Serializable {

    private static final long serialVersionUID = -5558226427711147363L;
    private String userId;
    private Integer userRole;
}
