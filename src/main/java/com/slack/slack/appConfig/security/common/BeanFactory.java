package com.slack.slack.appConfig.security.common;

import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;

/**
 * 보안을 위해 사용할 공통 Bean 정의
 * */
@Component
public class BeanFactory {
    // 패스워드 인코더
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public ModelMapper modelMapperWithReflection() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setFieldAccessLevel(Configuration.AccessLevel.PRIVATE).setFieldMatchingEnabled(true).setMatchingStrategy(MatchingStrategies.LOOSE);
        return modelMapper;
    }
}
