package xyz.kbws.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author kbws
 * @date 2024/11/24
 * @description: 生成的图片验证码
 */
@Data
public class CheckCodeVO implements Serializable {

    private static final long serialVersionUID = 4081290697544567030L;
    private String checkCode;
    private String checkCodeKey;
}
