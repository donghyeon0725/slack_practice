package com.slack.slack.appConfig.filter;

import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 에러 핸들러가 에러 처리를 하더라도
 * doFilter 이 후 로직은 작동한다.
 * 허나, 에러 상태를 가져올 방법은 없어 보인다.
 *
 * 특정, url 패턴을 제외하는 방법
 * https://github.com/spring-projects/spring-boot/issues/7426
 * GenericFilterBean 대신 OncePerRequestFilter를 사용해야 한다.
 * */
@Component
public class RequestLogFilter extends OncePerRequestFilter implements LogFilters { // GenericFilterBean
    /**
     * 매칭 되는 url이 있는지 확인
     * */
    private AntPathMatcher pathMatcher = new AntPathMatcher();

    private List<String> excludeUrlPatterns = Arrays.asList("/h2-console/**");

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res,
                                 FilterChain chain)
            throws IOException, ServletException {

        long start = new Date().getTime();
        chain.doFilter(req, res);
        long elapsed = new Date().getTime() - start;


        String url = ((HttpServletRequest) req).getRequestURI();
        String method = ((HttpServletRequest) req).getMethod();
        String message = "completed in " + elapsed + " ms";

        logger.debug("Request[url=" + url + ", method=" + method + " " + message + "]");
    }

    /**
     * 제외하고 싶은 패턴이 있을 경우
     * excludeUrlPatterns 에 패턴을 등록하고
     * 아래와 같은 메소드를 추가한다.
     * */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException
    {
        return excludeUrlPatterns.stream()
                .anyMatch(p -> pathMatcher.match(p, request.getServletPath()));
        // Populate excludeUrlPatterns on which one to exclude here
    }

}

