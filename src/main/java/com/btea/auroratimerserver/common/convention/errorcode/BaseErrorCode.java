package com.btea.auroratimerserver.common.convention.errorcode;

import lombok.AllArgsConstructor;

/**
 * @Author: TwentyFiveBTea
 * @Date: 2026/02/08 03:04
 * @Description: 基础错误码
 */
@AllArgsConstructor
public enum BaseErrorCode implements IErrorCode {

    // 成功状态响应码
    SUCCESS("0000000", "操作成功"),

    // 客户端错误码
    CLIENT_ERROR("0000100", "客户端异常"),

    // 服务端错误码
    SERVICE_ERROR("0000200", "服务端异常"),

    // 业务逻辑错误码
    USER_REGISTERED("0000300", "用户已注册"),
    PASSWORD_NOT_MATCH("0000301", "两次密码不一致"),
    USER_NOT_FOUND("0000302", "用户不存在");

    // 用户注册登陆错误码
    private final String code;
    private final String message;

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
