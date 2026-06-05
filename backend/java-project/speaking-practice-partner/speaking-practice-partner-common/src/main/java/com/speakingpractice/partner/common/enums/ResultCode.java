package com.speakingpractice.partner.common.enums;

import lombok.Getter;

/**
 * 响应状态码枚举
 */
@Getter
public enum ResultCode {

    /**
     * 操作成功
     */
    SUCCESS(200, "操作成功"),

    /**
     * 操作失败
     */
    FAIL(500, "操作失败"),

    /**
     * 参数错误
     */
    PARAM_ERROR(400, "参数错误"),

    /**
     * 未授权
     */
    UNAUTHORIZED(401, "未授权"),

    /**
     * 禁止访问
     */
    FORBIDDEN(403, "禁止访问"),

    /**
     * 资源不存在
     */
    NOT_FOUND(404, "资源不存在"),

    /**
     * 用户名或密码错误
     */
    LOGIN_ERROR(4001, "用户名或密码错误"),

    /**
     * Token无效
     */
    TOKEN_INVALID(4002, "Token无效"),

    /**
     * Token已过期
     */
    TOKEN_EXPIRED(4003, "Token已过期"),

    /**
     * 用户已存在
     */
    USER_EXISTS(4004, "用户已存在"),

    /**
     * 用户不存在
     */
    USER_NOT_EXISTS(4005, "用户不存在"),

    /**
     * 场景不存在
     */
    SCENE_NOT_EXISTS(5001, "场景不存在"),

    /**
     * 对话会话不存在
     */
    SESSION_NOT_EXISTS(6001, "对话会话不存在");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}
