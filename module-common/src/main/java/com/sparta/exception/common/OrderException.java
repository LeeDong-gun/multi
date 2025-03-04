package com.sparta.exception.common;

public class OrderException extends BusinessException {
    public OrderException(ErrorCode errorCode) {
        super(errorCode);
    }
}
