package com.sparta.exception.common;

public class EmailDuplicatedException extends BusinessException {

    public EmailDuplicatedException() {
        super(ErrorCode.DUPLICATE_EMAIL);
    }
}
