package xyz.kbws.common;

import lombok.Getter;

/**
 * @author kbws
 * @date 2024/11/24
 * @description: 自定义错误码
 */
@Getter
public enum ErrorCode {
    SUCCESS(0, "ok"),
    PARAMS_ERROR(40000, "请求参数错误"),
    NOT_LOGIN_ERROR(40100, "未登录"),
    NO_AUTH_ERROR(40100, "无权限"),
    TOO_MANY_REQUEST(40200, "请求过于频繁"),
    FORBIDDEN_ERROR(40300, "禁止访问"),
    NOT_FOUND_ERROR(40400, "请求数据不存在"),
    PARAM_NOT_VALID(40500, "参数错误"),
    NULL_ERROR(40600, "请求数据为空"),
    SERVICE_UNAVAILABLE(40800, "请求服务不可用"),
    SYSTEM_ERROR(50000, "系统内部异常"),
    TOKEN_ERROR(50100, "Token解析失败"),
    OPERATION_ERROR(50001, "操作失败");

    /**
     * 状态码
     */
    private final int code;

    /**
     * 信息
     */
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
