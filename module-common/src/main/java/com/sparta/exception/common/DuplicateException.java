package com.sparta.exception.common;

public class DuplicateException extends BusinessException {

    public DuplicateException(ErrorCode errorCode) {
        super(errorCode);
    }

    public DuplicateException(ErrorCode errorCode, String detail) {
        super(errorCode, detail);
    }
}
