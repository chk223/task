package com.example.task.common.exception;

import com.example.task.common.exception.code.ErrorCode;

public class AuthenticationException extends BaseException {

    public AuthenticationException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AuthenticationException() {
        super(ErrorCode.LOGIN_NOT_ACCEPTABLE);
    }
}
