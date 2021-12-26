package com.slack.slack.domain.service.impl;

import com.slack.slack.common.code.ErrorCode;
import com.slack.slack.common.code.Status;
import com.slack.slack.common.dto.team.TeamDTO;
import com.slack.slack.common.entity.*;
import com.slack.slack.common.exception.ResourceConflict;
import com.slack.slack.common.exception.ResourceNotFoundException;
import com.slack.slack.common.exception.UnauthorizedException;
import com.slack.slack.common.mail.MailServiceImpl;
import com.slack.slack.common.repository.*;
import com.slack.slack.domain.service.TeamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;
import testModule.WithMockCustomUser;

import javax.persistence.EntityManager;

import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
public class TeamServiceImplTest {

    @Autowired
    private TeamService teamService;

    @Autowired
    private TeamActivityRepository teamActivityRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private MailServiceImpl mailService;

    @Autowired
    private EntityManager em;

    private final String username = "tester@naver.com";

    private final String otherUsername = "other@naver.com";

    @BeforeEach
    public void saveUser() {
        Role role_user = Role.builder().roleName("USER").build();
        Role role_admin = Role.builder().roleName("ADMIN").build();

        User user = User.builder().email(username).build();
        user.getUserRoles().add(UserRole.builder().role(role_user).user(user).build());

        User other = User.builder().email(otherUsername).build();
        other.getUserRoles().add(UserRole.builder().role(role_user).user(other).build());

        em.persist(role_user);
        em.persist(role_admin);
        em.persist(user);
        em.persist(other);
        em.flush();
        em.clear();
    }


    @Test
    @DisplayName("팀 생성하기")
    @WithMockCustomUser(username = username)
    void team_test_case_1() {
        // TODO 정상적으로 팀 생성하기

        // given
        TeamDTO teamDTO = TeamDTO.builder().name("팀명").description("팀 설명").build();

        // when
        Integer savedTeamId = teamService.save(teamDTO);
        em.flush();
        em.clear();
        Team findTeam = em.find(Team.class, savedTeamId);


        List<TeamActivity> teamActivities = teamActivityRepository.findByTeam(findTeam);

        // then
        assertNotNull(findTeam, "팀이 정상 생성 되어야 함");
        assertEquals(teamActivities.size(), 1, "관련 활동이 하나 생성되어야 함");
    }

    @Test
    @DisplayName("팀 중복 생성 테스트")
    @WithMockCustomUser(username = username)
    void team_test_case_2() {
        // TODO 이미 생성한 팀이 있을 때, 팀을 생성할 수 없다.

        // given
        TeamDTO teamDTO = TeamDTO.builder().name("팀명").description("팀 설명").build();

        // when
        teamService.save(teamDTO);

        assertThrows(ResourceConflict.class, () -> teamService.save(teamDTO), "이미 생성한 팀이 있을 때, 중복으로 생성할 수 없다.");
    }


    @Test
    @DisplayName("팀 삭제하기")
    @WithMockCustomUser(username = username)
    void team_test_case_3() {
        // TODO 팀을 삭제하면 팀의 상태가 삭제 상태가 되어야 한다.

        // given
        User user = userRepository.findByEmail(username).orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));
        Team team = Team.builder().name("team").user(user).status(Status.CREATED).build();
        em.persist(team);

        TeamDTO teamDTO = TeamDTO.builder().id(team.getTeamId()).build();

        // when
        Integer deletedTeamId = teamService.delete(teamDTO);
        Team deletedTeam = em.find(Team.class, deletedTeamId);

        assertEquals(deletedTeam.getStatus(), Status.DELETED);
    }

    @Test
    @DisplayName("팀 삭제하기 : 권한이 없음")
    @WithMockCustomUser(username = otherUsername)
    void team_test_case_4() {
        // TODO 권한이 없이 팀을 삭제하려고 하면 예외가 발생한다.

        // given
        User user = userRepository.findByEmail(username).orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));
        Team teamOfUser = Team.builder().name("team").user(user).status(Status.CREATED).build();
        em.persist(teamOfUser);
        em.flush();
        em.clear();

        // when
        TeamDTO teamDTO = TeamDTO.builder().id(teamOfUser.getTeamId()).build();

        // then
        assertThrows(UnauthorizedException.class, () -> teamService.delete(teamDTO));
    }


    @Test
    @DisplayName("팀원 초대하기")
    @WithMockCustomUser(username = username)
    void team_test_case_5() throws Exception {
        // TODO 팀원 초대 메일을 보내면 sendInviteMail 을 호출하고 이메일을 받은 User 의 Entity를 반환해야한다.

        // given
        ReflectionTestUtils.setField(teamService, "mailService", mailService);

        User teamCreator = userRepository.findByEmail(username).orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));
        User to = userRepository.findByEmail(otherUsername).orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));
        Team teamOfCreator = Team.builder().name("team").user(teamCreator).status(Status.CREATED).build();
        Locale locale = Locale.getDefault();
        em.persist(teamOfCreator);
        em.flush();
        em.clear();
        TeamDTO teamDTO = TeamDTO.builder().id(teamOfCreator.getTeamId()).build();

        // when
        Integer invitedUserId = teamService.invite(to.getEmail(), teamDTO, locale);

        // then
        verify(mailService, times(1)).sendInviteMail(teamCreator.getEmail(), to.getEmail(), teamOfCreator, locale);
        assertEquals(to.getUserId(), invitedUserId);
    }

}
