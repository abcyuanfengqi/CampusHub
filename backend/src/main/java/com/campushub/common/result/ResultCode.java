package com.campushub.common.result;

import lombok.Getter;

/**
 * 响应状态码枚举
 *
 * @author campushub
 */
@Getter
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    ERROR(500, "系统内部错误"),
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未登录或token已过期"),
    FORBIDDEN(403, "没有权限"),
    NOT_FOUND(404, "资源不存在"),

    // 用户相关 1xxx
    USER_NOT_EXIST(1001, "用户不存在"),
    USER_PASSWORD_ERROR(1002, "密码错误"),
    USER_ALREADY_EXIST(1003, "用户已存在"),
    USER_DISABLED(1004, "用户已被禁用"),

    // 业务相关 2xxx
    STATUS_ERROR(2001, "状态不合法"),
    TIME_ERROR(2002, "时间不合法"),
    COUNT_FULL(2003, "人数已满"),

    // 系统相关 5xxx
    SYSTEM_ERROR(5000, "系统错误"),
    SERVICE_UNAVAILABLE(5003, "服务不可用");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}
