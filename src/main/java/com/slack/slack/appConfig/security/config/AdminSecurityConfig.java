package com.slack.slack.appConfig.security.config;

import com.slack.slack.appConfig.security.service.AccountContext;
import com.slack.slack.appConfig.security.service.AccountDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.cache.NullUserCache;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import java.util.Optional;

/**
 * 이 클래스는 url 자원을 관리하기 위한 관리자 시스템을 활성화 하기 위한 설정 파일 입니다.
 * */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Order(0)
public class AdminSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${admin.username}")
    private String id;

    @Value("${admin.password}")
    private String pw;

    @Value("${admin.loginProcessing}")
    private String login;

    @Value("${admin.logoutProcessing}")
    private String logout;

    @Value("${admin.remember}")
    private String remember;

    private String all = "/admin/**";

    private String root = "/admin";

    private String failure = "/admin/failure";

    private String denied = "/admin/denied";

    private final AccountDetailsServiceImpl accountDetailsService;

    // DB 연동
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService((UserDetailsService)accountDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 기본 설정. 사용자 인증에 HTTP Basic Authentication 을 사용합니다.
        http
                .httpBasic().disable();

        // 이 config 이 잡고 있는 url
        http
                .antMatcher(all)
                .authorizeRequests()
                .antMatchers(logout).permitAll()
                .antMatchers(all).hasRole("ADMIN")
                .anyRequest().authenticated();

        // 이 config 의 인증 정책
        http
                .formLogin()
                // 사용자 정의 로그인 페이지
                .loginPage(login)
                // 로그인 성공 했을 떄
                .defaultSuccessUrl(root)
                // 로그인 실패 했을 때
                .failureUrl(failure)
                // input id name
                .usernameParameter(id)
                // input password name
                .passwordParameter(pw)
                // 로그인 처리를 담당할 url (일치해야만 처리)
                .loginProcessingUrl(login)
                // 자격 증명에 성공 했을 때
                .successHandler((httpServletRequest, httpServletResponse, authentication) -> {
                    RequestCache requestCache = new HttpSessionRequestCache();
                    SavedRequest savedRequest = requestCache.getRequest(httpServletRequest, httpServletResponse);
                    httpServletResponse.sendRedirect(Optional.ofNullable(savedRequest).map(SavedRequest::getRedirectUrl).orElse(root));
                })
                // 자격 증명에 실패 했을 때
                /*.failureHandler((httpServletRequest, httpServletResponse, e) -> {
                    System.out.println("exception : " + e.getMessage());
                    httpServletResponse.sendRedirect("/login");
                })*/
                // 인증을 위한 자원에 대해서는 모든 요청을 허용한다.
                .permitAll();

        // Remember Me
        http
                .rememberMe()
                .rememberMeParameter(remember)
                .tokenValiditySeconds(3600 * 24 * 14)
                .alwaysRemember(true);


        // 로그아웃 정책
        http
                .logout()
                .logoutUrl(logout)
                .logoutSuccessUrl(login)
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID");

        // 세션 정책 - 동시 세션 제어
        http
                .sessionManagement()
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
                .expiredUrl(root);

        // 세션 정책 - 세션 고정 보호
        http
                .sessionManagement()
                .sessionFixation().changeSessionId();

        // 세션 기본 정책
        http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);

        // 인증 예외 처리
        http
                .exceptionHandling()
                // AccessDeniedHandler 를 구현한 구현체
                .accessDeniedHandler((httpServletRequest, httpServletResponse, e) -> {
                    httpServletResponse.sendRedirect(denied);
                });


        // SecurityContext 저장 전략 - other thread 에서 참조 가능
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }

    /**
     * static 자원
     * Spring Security 통과
     * */
    @Override
    public void configure(WebSecurity web) {
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

}
