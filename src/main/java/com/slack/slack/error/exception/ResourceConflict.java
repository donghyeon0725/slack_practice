package com.slack.slack.error.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 이미 존재하는 자원
 * */
@ResponseStatus(HttpStatus.CONFLICT)
public class ResourceConflict extends RuntimeException{
    private ErrorCode errorCode;
    public ResourceConflict(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
