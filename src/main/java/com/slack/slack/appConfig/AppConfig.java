package com.slack.slack.appConfig;

import com.slack.slack.filter.RequestLogFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class AppConfig {
    @Bean
    public FilterRegistrationBean<RequestLogFilter> requestLogFilterFilterRegistrationBean() {
        FilterRegistrationBean<RequestLogFilter> registration = new FilterRegistrationBean<>();

        // 로그 필터 생성
        RequestLogFilter filter = new RequestLogFilter();
        registration.setFilter(filter);
        registration.setOrder(Integer.MAX_VALUE);
        registration.setUrlPatterns(Arrays.asList("/*"));
        return registration;
    }


}
