package com.slack.slack.mapper;

import com.slack.slack.domain.board.Board;
import com.slack.slack.domain.board.BoardReturnDTO;
import com.slack.slack.domain.user.User;
import com.slack.slack.domain.user.UserReturnDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.TypeMap;
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
