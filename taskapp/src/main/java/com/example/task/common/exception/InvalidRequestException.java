package com.example.task.common.exception;

import com.example.task.common.exception.code.ErrorCode;

public class InvalidRequestException extends BaseException {

    public InvalidRequestException(ErrorCode errorCode) {
        super(errorCode);
    }

    public InvalidRequestException() {
        super(ErrorCode.INVALID_REQUEST);
    }
}
