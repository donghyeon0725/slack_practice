package com.slack.slack.appConfig.security.common;

import com.slack.slack.domain.board.Board;
import com.slack.slack.domain.board.BoardReturnDTO;
import com.slack.slack.domain.card.Card;
import com.slack.slack.domain.card.CardReturnDTO;
import com.slack.slack.domain.team.Team;
import com.slack.slack.domain.team.TeamReturnDTO;
import com.slack.slack.domain.user.User;
import com.slack.slack.domain.user.UserReturnDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
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


}
