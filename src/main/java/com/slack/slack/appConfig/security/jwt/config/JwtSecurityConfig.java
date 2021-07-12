package com.slack.slack.appConfig.security.jwt.config;

import com.slack.slack.appConfig.security.domain.hierarchy.SecurityRoleHierarchy;
import com.slack.slack.appConfig.security.domain.service.RoleHierarchyService;
import com.slack.slack.appConfig.security.domain.service.SecurityResourceService;
import com.slack.slack.appConfig.security.jwt.filter.JwtAuthenticationFilter;
import com.slack.slack.appConfig.security.jwt.handler.JwtAccessDeniedHandler;
import com.slack.slack.appConfig.security.jwt.interceptor.UrlFilterSecurityInterceptor;
import com.slack.slack.appConfig.security.jwt.metadata.UrlFilterInvocationSecurityMetadataSource;
import com.slack.slack.appConfig.security.jwt.metadata.UrlResourcesMapFactoryBean;
import com.slack.slack.appConfig.security.jwt.voter.IpAddressVoter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleHierarchyVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.GenericFilterBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Order(1)
public class JwtSecurityConfig extends WebSecurityConfigurerAdapter {

    private String all = "/**";

    @Value("${spring.security.secretKey}")
    private String secretKey;

    private final SecurityResourceService securityResourceService;

    private final RoleHierarchyService roleHierarchyService;

    private final RequestMatcher[] permitAllResources = {
            new AntPathRequestMatcher("/**", HttpMethod.OPTIONS.name())
            , new AntPathRequestMatcher("/h2-console*")
            , new AntPathRequestMatcher("/getImage*")
            , new AntPathRequestMatcher("/users/login/**")

            , new AntPathRequestMatcher("/users/join/**")
            , new AntPathRequestMatcher("/users/**")
            , new AntPathRequestMatcher("/socket/**")
            , new AntPathRequestMatcher("/rt/**")
            , new AntPathRequestMatcher("/teams/join")
            , new AntPathRequestMatcher("/teams/join/**")
    };

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 이 config 이 잡고 있는 url
        http
                .antMatcher(all)
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(abstractAuthenticationProcessingFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(filterSecurityInterceptor(), FilterSecurityInterceptor.class);


        // 이 config 의 기본 인증 정책
        http
                // rest api 만을 고려하여 기본 설정은 해제 => Authorization에 basic 항목은 사용하지 않음
                .httpBasic().disable()
                // csrf 보안 토큰 disable처리.
                .cors().disable()
                .csrf().disable()
                .rememberMe().disable()
                .formLogin().disable();


        // 세션 정책
        http
                // 토큰 기반 인증이므로 세션 역시 사용하지 않습니다.
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);


        http
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler());

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

    private GenericFilterBean abstractAuthenticationProcessingFilter() {
        return new JwtAuthenticationFilter(secretKey);
    }

    private AccessDeniedHandler accessDeniedHandler() {
        return new JwtAccessDeniedHandler();
    }

    @Bean
    public FilterSecurityInterceptor filterSecurityInterceptor() throws Exception {
        UrlFilterSecurityInterceptor interceptor = new UrlFilterSecurityInterceptor(permitAllResources);

        interceptor.setSecurityMetadataSource(urlFilterInvocationSecurityMetadataSource());
        interceptor.setAccessDecisionManager(affirmativeBased());
        interceptor.setAuthenticationManager(authenticationManagerBean());

        return interceptor;
    }

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
     * 하나라도 접근 거부가 뜨면
     * 허가 거부
     * */
    @Bean
    public AffirmativeBased affirmativeBased() {
        return new AffirmativeBased(getAccessDecisionVoters());
    }

    /**
     * 보터 리스트
     * */
    private List<AccessDecisionVoter<?>> getAccessDecisionVoters() {
        List<AccessDecisionVoter<?>> voters = new ArrayList<>();
        voters.add(new IpAddressVoter(securityResourceService));
        voters.add(roleVoter());
        return voters;
    }

    /**
     * 권한 계층의 정보를 setting 한 voter
     * */
    @Bean
    public AccessDecisionVoter<?> roleVoter() {
        return new RoleHierarchyVoter(roleHierarchy());
    }

    /**
     * 권한 계층
     * */
    @Bean
    public RoleHierarchyImpl roleHierarchy() {
        return new SecurityRoleHierarchy(roleHierarchyService);
    }
}
