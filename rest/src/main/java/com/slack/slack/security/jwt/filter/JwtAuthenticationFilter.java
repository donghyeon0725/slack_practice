package com.slack.slack.security.jwt.filter;

import com.slack.slack.security.jwt.common.JwtTokenResolver;
import com.slack.slack.security.jwt.provider.JwtAuthenticationProvider;
import com.slack.slack.security.jwt.token.JwtAuthenticationToken;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 인증 요청 정보로 인증 토큰을 만들고
 * 인증 관리자를 불러서 최종 반환된 토큰을 리턴하는 역할
 * */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenResolver tokenResolver = new JwtTokenResolver();

    private final JwtAuthenticationProvider authenticationProvider = new JwtAuthenticationProvider();

    private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new WebAuthenticationDetailsSource();

    private List<RequestMatcher> permitAllRequestMatcher = new ArrayList<>();

    private String secretKey;

    public JwtAuthenticationFilter(String secretKey, RequestMatcher... permitAllResources) {
        this.secretKey = secretKey;
        for (RequestMatcher config : permitAllResources) {
            permitAllRequestMatcher.add(config);
        }
    }

    private boolean isPermitAllResource(HttpServletRequest request) {
        for (RequestMatcher matcher : permitAllRequestMatcher) {
            // permit all resource 인 경우 anonymous 권한을 부여해서 인증 작업 없이 넘김
            if (matcher.matches(request)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = tokenResolver.resolveToken(request);

        // 허가 자원이 아니고 token 도 null 이 아닐때만 인증 시도
        if (!isPermitAllResource(request) && token != null) {
            // 인증 객체 set, 이 때 detail 도 setting 해주어야 함
            JwtAuthenticationToken authRequest = new JwtAuthenticationToken(token, secretKey);
            authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
            Authentication authenticated = authenticationProvider.authenticate(authRequest);

            SecurityContextHolder.getContext().setAuthentication(authenticated);
        }


        filterChain.doFilter(request, response);
    }
}
