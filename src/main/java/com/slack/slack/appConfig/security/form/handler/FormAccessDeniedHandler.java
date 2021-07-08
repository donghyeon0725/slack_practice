package com.slack.slack.appConfig.security.form.handler;

import io.jsonwebtoken.lang.Assert;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class FormAccessDeniedHandler implements AccessDeniedHandler {

    private String deniedPage;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
        Assert.notNull(deniedPage, "ErrorPage is Required");
        String deniedUrl = deniedPage + "?exception=" + e.getMessage();
        response.sendRedirect(deniedUrl);
    }

    public FormAccessDeniedHandler setDeniedPage(String deniedPage) {
        this.deniedPage = deniedPage;
        return this;
    }
}
