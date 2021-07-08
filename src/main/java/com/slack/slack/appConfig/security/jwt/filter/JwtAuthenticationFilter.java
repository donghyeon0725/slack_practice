package com.slack.slack.appConfig.security.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.slack.slack.appConfig.security.jwt.common.JwtTokenResolver;
import com.slack.slack.appConfig.security.jwt.token.JwtAuthenticationToken;
import com.slack.slack.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

/**
 * 인증 요청 정보로 인증 토큰을 만들고
 * 인증 관리자를 불러서 최종 반환된 토큰을 리턴하는 역할
 * */
public class JwtAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final JwtTokenResolver tokenResolver = new JwtTokenResolver();

    @Value("${spring.security.secretKey}")
    private String secretKey;


    public JwtAuthenticationFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {


        String token = tokenResolver.resolveToken(request);

        if (token == null)
            throw new IllegalArgumentException(ErrorCode.INVALID_INPUT_VALUE.getMessage());

        // 인증 객체 set, 이 때 detail 도 setting 해주어야 함
        JwtAuthenticationToken authRequest = new JwtAuthenticationToken(token, secretKey);
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));

        return this.getAuthenticationManager().authenticate(authRequest);
    }
}
