package com.btea.auroratimerserver.common.convention.exception;

import com.btea.auroratimerserver.common.convention.errorcode.BaseErrorCode;

/**
 * @Author: TwentyFiveBTea
 * @Date: 2026/02/08 03:04
 * @Description: 客户端异常
 */
public class ClientException extends AbstractException{

    public ClientException(String message) {
        super(message, null, BaseErrorCode.CLIENT_ERROR);
    }

    public ClientException(String message, Throwable throwable) {
        super(message, throwable, BaseErrorCode.CLIENT_ERROR);
    }
}
