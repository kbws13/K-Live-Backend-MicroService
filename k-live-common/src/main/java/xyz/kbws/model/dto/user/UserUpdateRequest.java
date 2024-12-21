package xyz.kbws.model.dto.user;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @author kbws
 * @date 2024/12/9
 * @description: 用户更新信息请求
 */
@Data
public class UserUpdateRequest implements Serializable {

    private static final long serialVersionUID = -8227236412637645687L;
    @NotEmpty(message = "昵称不能为空")
    @Size(max = 20, message = "昵称超过最大长度")
    private String nickName;
    @NotEmpty(message = "头像不能为空")
    @Size(max = 100, message = "头像超过最大长度")
    private String avatar;
    @NotNull(message = "性别不能为空")
    private Integer sex;
    @Size(max = 10)
    private String birthday;
    @Size(max = 150)
    private String school;
    @Size(max = 80)
    private String personIntroduction;
    @Size(max = 300)
    private String noticeInfo;
}
