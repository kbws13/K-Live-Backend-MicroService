package xyz.kbws.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author kbws
 * @date 2024/11/24
 * @description: 通用返回类
 */
@Data
public class BaseResponse<T> implements Serializable {

    private static final long serialVersionUID = -8140209322969150545L;
    private int code;
    private T data;
    private String message;

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public BaseResponse(int code, T data) {
        this(code, data, "");
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage());
    }
}
