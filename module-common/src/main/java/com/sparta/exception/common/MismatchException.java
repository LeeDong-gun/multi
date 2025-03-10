package com.sparta.exception.common;

public class MismatchException extends BusinessException {

    public MismatchException(ErrorCode errorCode) {
        super(errorCode);
    }

    public MismatchException(ErrorCode errorCode, String detail) {
        super(errorCode, detail);
    }
}
