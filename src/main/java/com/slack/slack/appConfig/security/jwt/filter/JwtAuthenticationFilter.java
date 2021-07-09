package com.slack.slack.appConfig.security.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.slack.slack.appConfig.security.jwt.common.JwtTokenResolver;
import com.slack.slack.appConfig.security.jwt.provider.JwtAuthenticationProvider;
import com.slack.slack.appConfig.security.jwt.token.JwtAuthenticationToken;
import com.slack.slack.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.filter.OncePerRequestFilter;
import org.yaml.snakeyaml.Yaml;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.Map;

/**
 * 인증 요청 정보로 인증 토큰을 만들고
 * 인증 관리자를 불러서 최종 반환된 토큰을 리턴하는 역할
 * */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenResolver tokenResolver = new JwtTokenResolver();

    private final JwtAuthenticationProvider authenticationProvider = new JwtAuthenticationProvider();

    private String secretKey;

    public JwtAuthenticationFilter(String secretKey) {
        this.secretKey = secretKey;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = tokenResolver.resolveToken(request);

        // preflight 로 때문에 Exception 을 throw 하면 안됨
        if (token != null) {
            // 인증 객체 set, 이 때 detail 도 setting 해주어야 함
            JwtAuthenticationToken authRequest = new JwtAuthenticationToken(token, secretKey);
            Authentication authenticated = authenticationProvider.authenticate(authRequest);

            SecurityContextHolder.getContext().setAuthentication(authenticated);
        }

        filterChain.doFilter(request, response);
    }
}
