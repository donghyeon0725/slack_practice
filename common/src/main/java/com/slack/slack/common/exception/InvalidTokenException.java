package com.slack.slack.common.exception;

import com.slack.slack.common.code.ErrorCode;

/**
 * 토큰이 무효합니다.
 * 시스템 예외
 *
 * @author 김동현
 * @version 1.0, 토큰 에러
 * */
public class InvalidTokenException extends RuntimeException {
    private ErrorCode errorCode;
    public InvalidTokenException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
