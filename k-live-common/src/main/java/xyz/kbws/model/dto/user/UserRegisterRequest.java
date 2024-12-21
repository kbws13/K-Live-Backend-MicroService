package xyz.kbws.model.dto.user;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author kbws
 * @date 2024/11/25
 * @description: 用户注册
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = -6284538547202624163L;
    /**
     * 昵称
     */
    @NotBlank(message = "昵称不能为空")
    private String nickName;
    /**
     * 邮箱
     */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;
    @NotBlank(message = "密码不能为空")
    private String checkPassword;
    @NotBlank(message = "验证码 key 不能为空")
    private String checkCodeKey;
    @NotBlank(message = "验证码不能为空")
    private String checkCode;
}
