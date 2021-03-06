package com.slack.slack.security.jwt.config;

import com.slack.slack.security.hierarchy.SecurityRoleHierarchy;
import com.slack.slack.common.service.RoleHierarchyService;
import com.slack.slack.common.service.SecurityResourceService;
import com.slack.slack.security.jwt.filter.JwtAuthenticationFilter;
import com.slack.slack.security.jwt.handler.JwtAccessDeniedHandler;
import com.slack.slack.security.jwt.interceptor.UrlFilterSecurityInterceptor;
import com.slack.slack.security.jwt.metadata.UrlFilterInvocationSecurityMetadataSource;
import com.slack.slack.security.jwt.factory.UrlResourcesMapFactoryBean;
import com.slack.slack.security.jwt.voter.IpAddressVoter;
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
            , new AntPathRequestMatcher("/users*")
            , new AntPathRequestMatcher("/socket/**")
            , new AntPathRequestMatcher("/socket*")
            , new AntPathRequestMatcher("/rt/**")
            , new AntPathRequestMatcher("/rt*")
            , new AntPathRequestMatcher("/teams/join")
            , new AntPathRequestMatcher("/teams/join/**")
    };

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // ??? config ??? ?????? ?????? url
        http
                .antMatcher(all)
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(abstractAuthenticationProcessingFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(filterSecurityInterceptor(), FilterSecurityInterceptor.class);


        // ??? config ??? ?????? ?????? ??????
        http
                // rest api ?????? ???????????? ?????? ????????? ?????? => Authorization??? basic ????????? ???????????? ??????
                .httpBasic().disable()
                // csrf ?????? ?????? disable??????.
                .cors().disable()
                .csrf().disable()
                .rememberMe().disable()
                .formLogin().disable();


        // ?????? ??????
        http
                // ?????? ?????? ??????????????? ?????? ?????? ???????????? ????????????.
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);


        http
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler());

        http
                .headers().frameOptions().disable();

        // SecurityContext ?????? ?????? - other thread ?????? ?????? ??????
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }

    // ????????? ??????
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    private GenericFilterBean abstractAuthenticationProcessingFilter() {
        return new JwtAuthenticationFilter(secretKey, permitAllResources);
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
     * ???????????? ?????? ????????? ??????
     * ?????? ??????
     * */
    @Bean
    public AffirmativeBased affirmativeBased() {
        return new AffirmativeBased(getAccessDecisionVoters());
    }

    /**
     * ?????? ?????????
     * */
    private List<AccessDecisionVoter<?>> getAccessDecisionVoters() {
        List<AccessDecisionVoter<?>> voters = new ArrayList<>();
        voters.add(new IpAddressVoter(securityResourceService));
        voters.add(roleVoter());
        return voters;
    }

    /**
     * ?????? ????????? ????????? setting ??? voter
     * */
    @Bean
    public AccessDecisionVoter<?> roleVoter() {
        return new RoleHierarchyVoter(roleHierarchy());
    }

    /**
     * ?????? ??????
     * */
    @Bean
    public RoleHierarchyImpl roleHierarchy() {
        return new SecurityRoleHierarchy(roleHierarchyService);
    }
}
