package com.example.task.common.exception;

import com.example.task.common.exception.code.ErrorCode;

public class AccessDeniedException extends BaseException {

    public AccessDeniedException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AccessDeniedException() {
        super(ErrorCode.ACCESS_DENIED);
    }
}
