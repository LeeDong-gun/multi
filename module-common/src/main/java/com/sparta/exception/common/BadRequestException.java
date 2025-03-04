package com.sparta.exception.common;

public class BadRequestException extends BusinessException {
	public BadRequestException(ErrorCode errorCode) {
		super(errorCode);
	}
}
