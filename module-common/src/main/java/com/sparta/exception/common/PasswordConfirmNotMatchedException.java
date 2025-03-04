package com.sparta.exception.common;

public class PasswordConfirmNotMatchedException extends BusinessException {

    public PasswordConfirmNotMatchedException() {
        super(ErrorCode.INVALID_PASSWORD_CONFIRM);
    }
}
