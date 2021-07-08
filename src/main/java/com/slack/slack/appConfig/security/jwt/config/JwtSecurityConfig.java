package com.slack.slack.appConfig.security.jwt.config;

import com.slack.slack.appConfig.security.jwt.common.JwtTokenResolver;
import com.slack.slack.appConfig.security.jwt.filter.JwtAuthenticationFilter;
import com.slack.slack.appConfig.security.jwt.provider.JwtAuthenticationProvider;
import com.slack.slack.appConfig.security.jwt.service.JwtUserDetailsService;
import com.slack.slack.appConfig.security.jwt.token.JwtAuthenticationToken;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Order(1)
public class JwtSecurityConfig extends WebSecurityConfigurerAdapter {

    private String all = "/test/**";

    private String url = "/test/*";

    private JwtUserDetailsService userDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 기본 설정. 사용자 인증에 HTTP Basic Authentication 을 사용합니다.
        http
                .httpBasic().disable();

        // 이 config 이 잡고 있는 url
        http
                .antMatcher(all)
                .authorizeRequests()
                .antMatchers(all).hasRole("USER")
                .anyRequest().authenticated();

        // 이 config 의 기본 인증 정책
        http
                // rest api 만을 고려하여 기본 설정은 해제 => Authorization에 basic 항목은 사용하지 않음
                .httpBasic().disable()
                // csrf 보안 토큰 disable처리.
                .cors().disable()
                .csrf().disable();

        // 세션 정책
        http
                // 토큰 기반 인증이므로 세션 역시 사용하지 않습니다.
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http
                .addFilterBefore(abstractAuthenticationProcessingFilter(), UsernamePasswordAuthenticationFilter.class);


        http
                .headers().frameOptions().disable();


        // SecurityContext 저장 전략 - other thread 에서 참조 가능
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }

    // 매니저 추가
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public AbstractAuthenticationProcessingFilter abstractAuthenticationProcessingFilter() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(url);
        filter.setAuthenticationManager(authenticationManagerBean());
        return filter;
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authenticationProvider());
    }

    private AuthenticationProvider authenticationProvider() {
        return new JwtAuthenticationProvider(userDetailsService);
    }
}
