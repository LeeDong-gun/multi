package com.sparta.exception.common;

public class PaymentException extends BusinessException {
  public PaymentException(ErrorCode errorCode) {
    super(errorCode);
  }

  public PaymentException(ErrorCode errorCode, String detail) {
    super(errorCode, detail);
  }
}


