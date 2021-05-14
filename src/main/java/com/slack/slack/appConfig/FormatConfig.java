package com.slack.slack.appConfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * return format
 *
 * json, xml
 * */
@EnableWebMvc
@Configuration
public class FormatConfig implements WebMvcConfigurer {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // xml 포멧을 지원하는 컨버터
        converters.add(new MappingJackson2XmlHttpMessageConverter());
        // json 포멧을 지원하는 컨버터
        converters.add(new MappingJackson2HttpMessageConverter());
        // plain/text 포멕을 지원하는 컨버터
        converters.add(new StringHttpMessageConverter());
    }

    /**
     * 기본 return format을 json으로
     * */
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.defaultContentType(MediaType.APPLICATION_JSON);
    }
}
