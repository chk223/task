package com.example.task.common.exception;

import com.example.task.common.exception.code.ErrorCode;

public class JwtException extends BaseException {

    public JwtException(ErrorCode errorCode) {
        super(errorCode);
    }

    public JwtException() {
        super(ErrorCode.INVALID_TOKEN);
    }
}
