package com.slack.slack.appConfig.security.config;

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
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.cache.NullUserCache;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

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

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("admin").password("{noop}1234").roles("ADMIN");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 기본 설정. 사용자 인증에 HTTP Basic Authentication 을 사용합니다.
        http
                .httpBasic();

        // 이 config 이 잡고 있는 url
        http
                .antMatcher(all)
                .authorizeRequests()
                .antMatchers(logout).permitAll()
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
                /*.successHandler((httpServletRequest, httpServletResponse, authentication) -> {
                    System.out.println("authentication : " + authentication.getName());
                    httpServletResponse.sendRedirect("/");
                })
                // 자격 증명에 실패 했을 때
                .failureHandler((httpServletRequest, httpServletResponse, e) -> {
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
        http.logout()
                .logoutUrl(logout)
                .logoutSuccessUrl(login)
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID");


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
