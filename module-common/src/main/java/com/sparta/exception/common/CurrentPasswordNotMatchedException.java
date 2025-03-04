package com.sparta.exception.common;

public class CurrentPasswordNotMatchedException extends BusinessException {

    public CurrentPasswordNotMatchedException() {
        super(ErrorCode.INVALID_CURRENT_PASSWORD);
    }
}
