package com.slack.slack.error.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 메일을 불러오는데 실패함
 * */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class MailLoadFailException extends RuntimeException {
    private ErrorCode errorCode;
    public MailLoadFailException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
