package com.slack.slack.appConfig;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 스웨거 ui를 찾는 경로가 3점대부터 변경 됨
 * http://localhost:8080/swagger-ui/index.html
 *
 * 참고링크 : https://github.com/springfox/springfox/issues/3360
 * */
@Configuration
@EnableSwagger2
public class SwaggerConfig implements WebMvcConfigurer {
    private static final String BASEPACKAGE = "com.slack.slack";
    private static final Contact DEFAULT_CONTACT = new Contact("", "", "ehdgus5015@gmail.com");
    private static final String TITLE = "Hello REST API";
    private static final String DESCRIPTION = "Some custom description of API.";
    private static final String VERSION = "API 1.0";
    private static final String TERMOFSERVICEURL = "Terms of service";
    private static final String LICENSE = "License of API";
    private static final String LICENSEURL = "API license URL";
    private static final String APPLICATION_XML = "application/json";
    private static final String APPLICATION_JSON = "application/xml";
    // 사용자가 요청할 수 있는 데이터 타입
    private static final Set<String> DEFAULT_PRODUCES_AND_CONSUMES = new HashSet<>(Arrays.asList(APPLICATION_XML, APPLICATION_JSON));

    // 스웨거 기본 정보
    private static final ApiInfo DEFAULT_API_INFO = new ApiInfo(
                                                TITLE,
                                                DESCRIPTION,
                                                VERSION,
                                                TERMOFSERVICEURL,
                                                DEFAULT_CONTACT,
                                                LICENSE,
                                                LICENSEURL,
                                                Collections.emptyList());

    /**
     * api 문서 화면에 보여질, 글과 컨트롤러를 정의합니다.
     * */
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage(BASEPACKAGE))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(DEFAULT_API_INFO)
                .produces(DEFAULT_PRODUCES_AND_CONSUMES)
                .consumes(DEFAULT_PRODUCES_AND_CONSUMES)
                ;
    }



}

