package com.sparta.exception.common;

public class PasswordIncorrectException extends BusinessException {
  
    public PasswordIncorrectException() {
        super(ErrorCode.PASSWORD_INCORRECT);
    }
}
