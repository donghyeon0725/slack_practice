package com.slack.slack.config;

import com.slack.slack.common.code.Encoding;
import com.slack.slack.common.code.Path;
import com.slack.slack.config.filter.LogFilters;
import lombok.AllArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.Arrays;

/**
 * log filter
 * <p>
 * encoding filter
 */
@AllArgsConstructor
@Configuration
public class AppConfig {

    /**
     * 로그 필터
     */
    private LogFilters logFilters;

    @Bean
    public FilterRegistrationBean requestLogFilterFilterRegistrationBean() {
        FilterRegistrationBean registration = new FilterRegistrationBean<>();

        // 로그 필터 생성
        //RequestLogFilter filter = new RequestLogFilter();
        registration.setFilter((OncePerRequestFilter) logFilters);
        registration.setOrder(Integer.MAX_VALUE);
        registration.setUrlPatterns(Arrays.asList(Path.ALL.get()));
        return registration;
    }

    @Bean
    public FilterRegistrationBean EncodingFilterFilterRegistrationBean() {
        FilterRegistrationBean registration = new FilterRegistrationBean<>();

        // 인코딩 필터
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setForceEncoding(true);
        characterEncodingFilter.setEncoding(Encoding.UTF8.get());

        registration.setFilter(characterEncodingFilter);
        return registration;
    }

    /**
     * _method 키워드로 METHOD 구분 가능하도록 해줌
     * */
    @Bean
    public FilterRegistrationBean httpMethodFilter() {
        FilterRegistrationBean filter = new FilterRegistrationBean();
        filter.setFilter(new HiddenHttpMethodFilter());
        filter.setName("httpMethodFilter");
        filter.addUrlPatterns("/*");
        return filter;
    }


}
