package com.slack.slack.security.jwt.handler;

import com.slack.slack.common.code.ErrorCode;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) {
        throw new AccessDeniedException(ErrorCode.UNAUTHORIZED_VALUE.getMessage());
    }
}
