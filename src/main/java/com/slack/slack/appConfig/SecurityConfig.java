package com.slack.slack.appConfig;

import com.slack.slack.appConfig.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.slack.slack.appConfig.security.*;

/**
 * 스프링 시큐리티 관련 설정
 * */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    // find 키워드가 붙었기 때문에 @RequiredArgsConstructor 에 의해 모듈이 추가된다.
    private final JwtTokenProvider jwtTokenProvider;


    /**
     * 암호화에 필요한 PasswordEncoder 를 Bean에 등록합니다.
     * */
    @Bean
    public PasswordEncoder passwordEncoder() {
        /**
         * 인코더 목록이 들어있어서 필요한 것을 꺼내어 쓰면 된다!
         * */
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /**
     * authenticationManager를 Bean에 등록합니다.
     * */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


    /**
     * h2-console 로 들어오는 모든 요청을 허용
     *
     * url 요청에 대한 인증을 요구합니다.
     * */
    protected void configure(HttpSecurity http) throws Exception {


        http
                // rest api 만을 고려하여 기본 설정은 해제 => Authorization에 basic 항목은 사용하지 않음
                .httpBasic().disable()
                // csrf 보안 토큰 disable처리.
                .csrf().disable()
                // 토큰 기반 인증이므로 세션 역시 사용하지 않습니다.
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // 요청에 대한 사용권한 체크
                .authorizeRequests()
                // DB 요청에 대한 응답은 허용
                .antMatchers("/h2-console/**").permitAll()
                // 로그인, 회원가입은 권한 필요 없음.
                .antMatchers("/users/login/**").permitAll()
                .antMatchers("/users/join/**").permitAll()
                .antMatchers("/users").permitAll()
                .antMatchers("/team").hasRole("USER")
                // users url에 대한 요청은 USER 권한을 요청
//                .antMatchers("/users/**").hasRole("USER")
                // 그외 나머지 요청은 누구나 접근 가능
                .anyRequest().permitAll()
                .and()
                // 만든  필터 등록
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class);
        // JwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter 전에 넣는다


        http.headers().frameOptions().disable();
    }
}
