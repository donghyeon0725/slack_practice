package com.slack.admin.security.form.config;


import com.slack.admin.security.form.details.FormWebAuthenticationDetails;
import com.slack.admin.security.form.factory.UrlResourcesMapFactoryBean;
import com.slack.admin.security.form.handler.FormAccessDeniedHandler;
import com.slack.admin.security.form.handler.FormAuthenticationFailureHandler;
import com.slack.admin.security.form.handler.FormAuthenticationSuccessHandler;
import com.slack.admin.security.form.hierarchy.SecurityRoleHierarchy;
import com.slack.admin.security.form.metadata.UrlFilterInvocationSecurityMetadataSource;
import com.slack.admin.security.form.provider.FormAuthenticationProvider;
import com.slack.admin.security.form.service.AccountDetailsService;
import com.slack.slack.common.service.RoleHierarchyService;
import com.slack.slack.common.service.SecurityResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;

/**
 * ??? ???????????? url ????????? ???????????? ?????? ????????? ???????????? ????????? ?????? ?????? ?????? ?????? ?????????.
 * */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
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

    private String all = "/**";

    private String root = "/";

    private String failure = "/failure";

    private String denied = "/denied";

    private final AccountDetailsService accountDetailsService;

    private final PasswordEncoder passwordEncoder;

    private final RoleHierarchyService roleHierarchyService;

    private final SecurityResourceService securityResourceService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // ?????? ??????. ????????? ????????? HTTP Basic Authentication ??? ???????????????.
        http
                .httpBasic().disable();

        // ??? config ??? ?????? ?????? url
        http
                .antMatcher(all)
                .authorizeRequests()
                .antMatchers(failure).permitAll()
                .antMatchers(logout).permitAll()
                .antMatchers(all).hasRole("ADMIN")
                .anyRequest().authenticated();

        // ??? config ??? ?????? ??????
        http
                .formLogin()
                // detail
                .authenticationDetailsSource(authenticationDetailsSource())
                // ????????? ?????? ????????? ?????????
                .loginPage(login)
                // ????????? ?????? ?????? ???
                .defaultSuccessUrl(root)
                // ????????? ?????? ?????? ???
//                .failureUrl(failure)
                // input id name
                .usernameParameter(id)
                // input password name
                .passwordParameter(pw)
                // ????????? ????????? ????????? url (??????????????? ??????)
                .loginProcessingUrl(login)
                // ?????? ????????? ?????? ?????? ???
                .successHandler(authenticationSuccessHandler())
                // ?????? ????????? ?????? ?????? ???
                .failureHandler(authenticationFailureHandler())
                // ????????? ?????? ????????? ???????????? ?????? ????????? ????????????.
                .permitAll();

        // Remember Me
        http
                .rememberMe()
                .rememberMeParameter(remember)
                .tokenValiditySeconds(3600 * 24 * 14)
                .userDetailsService(accountDetailsService);


        // ???????????? ??????
        http
                .logout()
                .logoutUrl(logout)
                .logoutSuccessUrl(login)
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID");

        // ?????? ?????? - ?????? ?????? ??????
        http
                .sessionManagement()
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
                .expiredUrl(root);

        // ?????? ?????? - ?????? ?????? ??????
        http
                .sessionManagement()
                .sessionFixation().changeSessionId();

        // ?????? ?????? ??????
        http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);

        // ?????? ?????? ??????
        http
                .exceptionHandling()
                // AccessDeniedHandler ??? ????????? ?????????
                .accessDeniedHandler(accessDeniedHandler());


        // SecurityContext ?????? ?????? - other thread ?????? ?????? ??????
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }

    /**
     * static ??????
     * Spring Security ??????
     * */
    @Override
    public void configure(WebSecurity web) {
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    private AuthenticationProvider authenticationProvider() {
        return new FormAuthenticationProvider(accountDetailsService, passwordEncoder);
    }

    private AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> authenticationDetailsSource() {
        return request -> new FormWebAuthenticationDetails(request);
    }

    private AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new FormAuthenticationSuccessHandler().setSuccessUrl(root);
    };

    private AuthenticationFailureHandler authenticationFailureHandler() {
        return new FormAuthenticationFailureHandler().setFailureUrl(failure);
    };

    private AccessDeniedHandler accessDeniedHandler() {
        return new FormAccessDeniedHandler().setDeniedPage(denied);
    };


    @Bean
    public UrlFilterInvocationSecurityMetadataSource urlFilterInvocationSecurityMetadataSource() {
        return new UrlFilterInvocationSecurityMetadataSource(urlResourcesMapFactoryBean().getObject(), securityResourceService);
    }

    private UrlResourcesMapFactoryBean urlResourcesMapFactoryBean() {
        UrlResourcesMapFactoryBean urlResourcesMapFactoryBean = new UrlResourcesMapFactoryBean();
        urlResourcesMapFactoryBean.setSecurityResourceService(securityResourceService);

        return urlResourcesMapFactoryBean;
    }

    /**
     * ?????? ??????
     * */
    @Bean
    public RoleHierarchyImpl roleHierarchy() {
        return new SecurityRoleHierarchy(roleHierarchyService);
    }
}
