package com.slack.admin.security.form.handler;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.util.Assert;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class FormAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    // 사용자 이전 요청에 대한
    private RequestCache requestCache = new HttpSessionRequestCache();
    // 사용자 페이지 제어에 사용할 수 있다.
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    private String successUrl;

    /**
     * Authentication 객체를 받기 때문에 원하는 작업을 추가적으로 할 수도 있을 것이다.
     * */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Assert.notNull(successUrl, "Authentication Success URL Could Not Found");
        // default url 설정하기
        setDefaultTargetUrl(successUrl);

        SavedRequest savedRequest = requestCache.getRequest(request, response);
        // 바로 로그인 페이지로 왔던 경우에는 null 일수 있다
        response.sendRedirect(Optional.ofNullable(savedRequest).map(SavedRequest::getRedirectUrl).orElse(getDefaultTargetUrl()));
    }

    public FormAuthenticationSuccessHandler setSuccessUrl(String successUrl) {
        this.successUrl = successUrl;
        return this;
    }
}
