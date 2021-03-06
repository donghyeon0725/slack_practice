package com.slack.slack.domain.service.impl;

import com.slack.slack.common.code.ErrorCode;
import com.slack.slack.common.code.Status;
import com.slack.slack.common.dto.team.TeamCommand;
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
    @DisplayName("??? ????????????")
    @WithMockCustomUser(username = username)
    void team_test_case_1() {
        // TODO ??????????????? ??? ????????????

        // given
        TeamCommand teamCommand = TeamCommand.builder().name("??????").description("??? ??????").build();

        // when
        Integer savedTeamId = teamService.save(teamCommand);
        em.flush();
        em.clear();
        Team findTeam = em.find(Team.class, savedTeamId);


        List<TeamActivity> teamActivities = teamActivityRepository.findByTeam(findTeam);

        // then
        assertNotNull(findTeam, "?????? ?????? ?????? ????????? ???");
        assertEquals(teamActivities.size(), 1, "?????? ????????? ?????? ??????????????? ???");
    }

    @Test
    @DisplayName("??? ?????? ?????? ?????????")
    @WithMockCustomUser(username = username)
    void team_test_case_2() {
        // TODO ?????? ????????? ?????? ?????? ???, ?????? ????????? ??? ??????.

        // given
        TeamCommand teamCommand = TeamCommand.builder().name("??????").description("??? ??????").build();

        // when
        teamService.save(teamCommand);

        assertThrows(ResourceConflict.class, () -> teamService.save(teamCommand), "?????? ????????? ?????? ?????? ???, ???????????? ????????? ??? ??????.");
    }


    @Test
    @DisplayName("??? ????????????")
    @WithMockCustomUser(username = username)
    void team_test_case_3() {
        // TODO ?????? ???????????? ?????? ????????? ?????? ????????? ????????? ??????.

        // given
        User user = userRepository.findByEmail(username).orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));
        Team team = Team.builder().name("team").user(user).status(Status.CREATED).build();
        em.persist(team);

        TeamCommand teamCommand = TeamCommand.builder().id(team.getTeamId()).build();

        // when
        Integer deletedTeamId = teamService.delete(teamCommand);
        Team deletedTeam = em.find(Team.class, deletedTeamId);

        assertEquals(deletedTeam.getStatus(), Status.DELETED);
    }

    @Test
    @DisplayName("??? ???????????? : ????????? ??????")
    @WithMockCustomUser(username = otherUsername)
    void team_test_case_4() {
        // TODO ????????? ?????? ?????? ??????????????? ?????? ????????? ????????????.

        // given
        User user = userRepository.findByEmail(username).orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));
        Team teamOfUser = Team.builder().name("team").user(user).status(Status.CREATED).build();
        em.persist(teamOfUser);
        em.flush();
        em.clear();

        // when
        TeamCommand teamCommand = TeamCommand.builder().id(teamOfUser.getTeamId()).build();

        // then
        assertThrows(UnauthorizedException.class, () -> teamService.delete(teamCommand));
    }


    @Test
    @DisplayName("?????? ????????????")
    @WithMockCustomUser(username = username)
    void team_test_case_5() throws Exception {
        // TODO ?????? ?????? ????????? ????????? sendInviteMail ??? ???????????? ???????????? ?????? User ??? Entity??? ??????????????????.

        // given
        ReflectionTestUtils.setField(teamService, "mailService", mailService);

        User teamCreator = userRepository.findByEmail(username).orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));
        User to = userRepository.findByEmail(otherUsername).orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));
        Team teamOfCreator = Team.builder().name("team").user(teamCreator).status(Status.CREATED).build();
        Locale locale = Locale.getDefault();
        em.persist(teamOfCreator);
        em.flush();
        em.clear();

        // when
        Integer invitedUserId = teamService.invite(teamOfCreator.getTeamId(), to.getEmail(), locale);

        // then
        verify(mailService, times(1)).sendInviteMail(teamCreator.getEmail(), to.getEmail(), teamOfCreator, locale);
        assertEquals(to.getUserId(), invitedUserId);
    }

}
