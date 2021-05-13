package com.slack.slack.appConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    /**
     * 인메모리에 사용자 저장소를 만드는데,
     * 아이디 username
     * 비밀번호 passw0rd로 들어온 사용자의 권한을
     * ADMIN으로 하여 인메모리 저장소에 저장해두겠다는 말이다.
     *
     * 이 부분에서 {noop} 부분을 적절하게 변경하면
     * 사용자 별로, 리소스별로 권한을 세세하게 부여할 수 있다.
     * */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("username")
                .password("{noop}passw0rd")
                .roles("ADMIN");
    }

    /**
     * h2-console 로 들어오는 모든 요청을 허용하는 로직
     * */
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/h2-console/**").permitAll();
        http.csrf().disable();
        http.headers().frameOptions().disable();
    }
}
