package com.btea.auroratimerserver.common.convention.result;

import com.btea.auroratimerserver.common.convention.errorcode.BaseErrorCode;
import com.btea.auroratimerserver.common.convention.exception.AbstractException;

import java.util.Optional;

/**
 * @Author: TwentyFiveBTea
 * @Date: 2026/2/8 02:59
 * @Description: 全局返回对象构造器
 */
public class Results {
    /**
     * 构造成功响应
     */
    public static Result<Void> success() {
        return new Result<Void>()
                .setCode(BaseErrorCode.SUCCESS.code())
                .setMessage(BaseErrorCode.SUCCESS.message());
    }

    /**
     * 构造带返回数据的成功响应
     */
    public static <T> Result<T> success(T data) {
        return new Result<T>()
                .setCode(BaseErrorCode.SUCCESS.code())
                .setMessage(BaseErrorCode.SUCCESS.message())
                .setData(data);
    }

    /**
     * 构建服务端失败响应
     */
    public static Result<Void> failure() {
        return new Result<Void>()
                .setCode(BaseErrorCode.SERVICE_ERROR.code())
                .setMessage(BaseErrorCode.SERVICE_ERROR.message());
    }

    /**
     * 构造失败响应
     */
    public static Result<Void> failure(String code, String message) {
        return new Result<Void>()
                .setCode(code)
                .setMessage(message);
    }

    /**
     * 通过 {@link AbstractException} 构建失败响应
     */
    public static Result<Void> failure(AbstractException abstractException) {
        String errorCode = Optional.ofNullable(abstractException.getErrorCode())
                .orElse(BaseErrorCode.SERVICE_ERROR.code());
        String errorMessage = Optional.ofNullable(abstractException.getErrorMessage())
                .orElse(BaseErrorCode.SERVICE_ERROR.message());
        return new Result<Void>()
                .setCode(errorCode)
                .setMessage(errorMessage);
    }
}
