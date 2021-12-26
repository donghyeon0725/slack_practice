package com.slack.slack.domain.service.impl;

import com.slack.slack.common.code.Status;
import com.slack.slack.common.entity.Team;
import com.slack.slack.common.entity.User;
import com.slack.slack.domain.service.CardService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;

import java.util.Date;

/**
 * 실패한 케이스 위주로 작성
 */
// TODO test application.yml  만들기
@ActiveProfiles("test")
@SpringBootTest
class CardServiceImplTest {

    @Autowired
    private CardService cardService;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("카드 생성")
    public void createCard() {
        User user = User.builder().name("유저").email("email@test.com").date(new Date()).status(Status.CREATED).password("1234").build();
        entityManager.persist(user);
        Team team = Team.builder().name("팀").date(new Date()).description("팀 입니다.").user(user).status(Status.CREATED).build();
        entityManager.persist(team);



//        cardService.create(new MockHttpServletRequest(), )
    }

}
