package com.slack.slack.domain.team;


import com.slack.slack.common.dto.team.TeamDTO;
import com.slack.slack.common.entity.Team;
import com.slack.slack.common.entity.validator.TeamValidator;
import com.slack.slack.common.repository.TeamActivityRepository;
import com.slack.slack.common.repository.TeamChatRepository;
import com.slack.slack.common.repository.TeamMemberRepository;
import com.slack.slack.common.repository.TeamRepository;
import com.slack.slack.common.util.TokenManager;
import com.slack.slack.common.util.SuccessAuthentication;
import com.slack.slack.common.entity.User;
import com.slack.slack.domain.service.impl.TeamServiceImpl;
import com.slack.slack.common.repository.UserRepository;
import com.slack.slack.common.exception.ResourceConflict;
import com.slack.slack.common.exception.UnauthorizedException;
import com.slack.slack.common.mail.MailService;
import com.slack.slack.common.code.State;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 유니 테스트
 *
 * Team Service Test
 *
 * @author 김동현
 */
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TeamServiceImplTest {

    // static 객체로 생성
    private static MockedStatic<SuccessAuthentication> successAuthentication;

    @BeforeAll
    public static void BeforeAll() {
        successAuthentication = mockStatic(SuccessAuthentication.class);
    }



    @Test
    @DisplayName("팀 생성하기")
    void team_create_case_1(
            @Mock TeamRepository teamRepository,
            @Mock UserRepository userRepository,
            @Mock TeamMemberRepository teamMemberRepository,
            @Mock TeamActivityRepository teamActivityRepository,
            @Mock TeamChatRepository teamChatRepository,
            @Mock ApplicationContext applicationContext,
            @Mock MailService mailService,
            @Mock TokenManager tokenManager
    ) {
        // TODO 정상적으로 팀 생성하기

        // given
        TeamServiceImpl teamService = new TeamServiceImpl(teamRepository, userRepository, teamMemberRepository, mailService, tokenManager, new TeamValidator(teamMemberRepository, teamRepository));

        TeamDTO teamDTO = TeamDTO.builder().name("팀명").description("팀 설명").build();

        User user = User.builder().email("testtest@test.com").id(1).build();


        // when
        when(userRepository.findByEmail(SuccessAuthentication.getPrincipal(String.class))).thenReturn(Optional.of(user));
        when(teamRepository.save(any())).thenAnswer((Answer) invocation -> invocation.getArguments()[0]);
        when(teamRepository.findByUser(user)).thenReturn(Optional.of(new ArrayList()));
        when(teamMemberRepository.save(any())).thenAnswer((Answer) invocation -> invocation.getArguments()[0]);


        assertNotNull(teamService.save(teamDTO));
    }

    @Test
    @DisplayName("팀 중복 생성")
    void team_create_case_2(
            @Mock TeamRepository teamRepository,
            @Mock UserRepository userRepository,
            @Mock TeamMemberRepository teamMemberRepository,
            @Mock TeamActivityRepository teamActivityRepository,
            @Mock TeamChatRepository teamChatRepository,
            @Mock ApplicationContext applicationContext,
            @Mock MailService mailService,
            @Mock TokenManager tokenManager
    ) {
        // TODO 이미 생성한 팀이 있을 때, 팀을 생성할 수 없다.

        // given
        TeamServiceImpl teamService = new TeamServiceImpl(teamRepository, userRepository, teamMemberRepository, mailService, tokenManager, new TeamValidator(teamMemberRepository, teamRepository));

        TeamDTO teamDTO = TeamDTO.builder().name("팀명").description("팀 설명").build();

        User user = User.builder().email("testtest@test.com").id(1).build();
        Team team = Team.builder().id(1).name("팀명").description("test").build();

        // when
        when(userRepository.findByEmail(SuccessAuthentication.getPrincipal(String.class))).thenReturn(Optional.of(user));
        when(teamRepository.findByUser(user)).thenReturn(Optional.of(Arrays.asList(team)));


        assertThrows(ResourceConflict.class, () -> teamService.save(teamDTO));
    }


    @Test
    @DisplayName("팀 삭제하기")
    void team_create_case_3(
            @Mock TeamRepository teamRepository,
            @Mock UserRepository userRepository,
            @Mock TeamMemberRepository teamMemberRepository,
            @Mock TeamActivityRepository teamActivityRepository,
            @Mock TeamChatRepository teamChatRepository,
            @Mock ApplicationContext applicationContext,
            @Mock MailService mailService,
            @Mock TokenManager tokenManager
    ) {
        // TODO 팀을 삭제하면 팀의 상태가 삭제 상태가 되어야 한다.

        // given
        TeamServiceImpl teamService = new TeamServiceImpl(teamRepository, userRepository, teamMemberRepository, mailService, tokenManager, new TeamValidator(teamMemberRepository, teamRepository));

        TeamDTO teamDTO = TeamDTO.builder().id(1).name("팀명").description("팀 설명").build();

        User user = User.builder().email("testtest@test.com").id(1).build();
        Team team = Team.builder().id(1).name("팀명").description("팀 설명").user(user).build();

        when(userRepository.findByEmail(SuccessAuthentication.getPrincipal(String.class))).thenReturn(Optional.of(user));
        when(teamRepository.findById(teamDTO.getId())).thenReturn(Optional.of(team));

        // when
        Team deletedTeam = teamService.delete(teamDTO);

        assertEquals(deletedTeam.getState(), State.DELETED);
    }

    @Test
    @DisplayName("팀 삭제하기 : 권한이 없음")
    void team_create_case_4(
            @Mock TeamRepository teamRepository,
            @Mock UserRepository userRepository,
            @Mock TeamMemberRepository teamMemberRepository,
            @Mock TeamActivityRepository teamActivityRepository,
            @Mock TeamChatRepository teamChatRepository,
            @Mock ApplicationContext applicationContext,
            @Mock MailService mailService,
            @Mock TokenManager tokenManager
    ) {
        // TODO 권한이 없이 팀을 삭제하려고 하면 예외가 발생한다.

        // given
        TeamServiceImpl teamService = new TeamServiceImpl(teamRepository, userRepository, teamMemberRepository, mailService, tokenManager, new TeamValidator(teamMemberRepository, teamRepository));

        TeamDTO teamDTO = TeamDTO.builder().id(1).name("팀명").description("팀 설명").build();
        User user = User.builder().email("testtest@test.com").id(1).build();
        User other = User.builder().email("testt123est@test.com").id(2).build();
        Team team = Team.builder().id(1).name("팀명").description("팀 설명").user(user).build();

        // 다른 이메일 팀 삭제 시도
        when(userRepository.findByEmail(SuccessAuthentication.getPrincipal(String.class))).thenReturn(Optional.of(other));
        when(teamRepository.findById(teamDTO.getId())).thenReturn(Optional.of(team));

        assertThrows(UnauthorizedException.class, () -> teamService.delete(teamDTO));
    }

    @Test
    @DisplayName("팀원 초대하기")
    void team_create_case_5(
            @Mock TeamRepository teamRepository,
            @Mock UserRepository userRepository,
            @Mock TeamMemberRepository teamMemberRepository,
            @Mock TeamActivityRepository teamActivityRepository,
            @Mock TeamChatRepository teamChatRepository,
            @Mock ApplicationContext applicationContext,
            @Mock MailService mailService,
            @Mock TokenManager tokenManager
    ) {
        // TODO 팀원 초대 메일을 보내면 sendInviteMail 을 호출하고 이메일을 받은 User 의 Entity를 반환해야한다.

        // given
        TeamServiceImpl teamService = new TeamServiceImpl(teamRepository, userRepository, teamMemberRepository, mailService, tokenManager, new TeamValidator(teamMemberRepository, teamRepository));

        User to = User.builder().email("altest@test.com").id(2).build();
        TeamDTO teamDTO = TeamDTO.builder().id(1).name("팀명").description("팀 설명").build();
        User teamCreator = User.builder().email("testtest@test.com").id(1).build();
        Team team = Team.builder().id(1).name("팀명").description("팀 설명").user(teamCreator).build();
        Locale locale = Locale.getDefault();


        when(SuccessAuthentication.getPrincipal(String.class)).thenReturn(teamCreator.getEmail());
        when(userRepository.findByEmail(SuccessAuthentication.getPrincipal(String.class))).thenReturn(Optional.of(teamCreator));
        when(userRepository.findByEmail(to.getEmail())).thenReturn(Optional.of(to));
        when(teamRepository.findById(teamDTO.getId())).thenReturn(Optional.of(team));

        User invited = teamService.invite(to.getEmail(), teamDTO, locale);

        verify(mailService, times(1)).sendInviteMail(teamCreator.getEmail(), to.getEmail(), team, locale);

        assertEquals(to, invited);
    }


}
