package com.sparta.exception.common;

public class PageOutOfBoundsException extends BusinessException {
	public PageOutOfBoundsException(ErrorCode errorCode) {
		super(errorCode);
	}
}
