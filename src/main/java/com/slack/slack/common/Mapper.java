package com.slack.slack.common;

import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class Mapper {

    @Bean
    public ModelMapper modelMapperWithReflection() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setFieldAccessLevel(Configuration.AccessLevel.PRIVATE).setFieldMatchingEnabled(true).setMatchingStrategy(MatchingStrategies.LOOSE);

        return modelMapper;
    }
}
