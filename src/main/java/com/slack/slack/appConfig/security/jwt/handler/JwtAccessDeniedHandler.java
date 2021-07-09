package com.slack.slack.appConfig.security.jwt.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.slack.slack.error.exception.ErrorCode;
import io.jsonwebtoken.lang.Assert;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) {
        throw new AccessDeniedException(ErrorCode.UNAUTHORIZED_VALUE.getMessage());
    }
}
