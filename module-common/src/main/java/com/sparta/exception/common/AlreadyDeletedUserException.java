package com.sparta.exception.common;

public class AlreadyDeletedUserException extends BusinessException {

    public AlreadyDeletedUserException() {
        super(ErrorCode.ALREADY_DELETED_USER);
    }
}
