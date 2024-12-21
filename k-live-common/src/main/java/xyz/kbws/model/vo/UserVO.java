package xyz.kbws.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author kbws
 * @date 2024/11/25
 * @description:
 */
@Data
public class UserVO implements Serializable {

    private static final long serialVersionUID = 3676406746132868131L;
    private String id;
    private String nickName;
    private String avatar;
    private Integer sex;
    private String userRole;
    private String personIntroduction;
    private String noticeInfo;
    private String birthday;
    private String school;
    private Long expireAt;
    private String token;
    private Integer fansCount;
    private Integer focusCount;
    private Integer likeCount;
    private Integer playCount;
    private Boolean haveFocus;
    private Integer theme;
    private Integer currentCoinCount;
}
