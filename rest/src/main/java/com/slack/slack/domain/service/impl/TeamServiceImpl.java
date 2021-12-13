package com.slack.slack.domain.service.impl;

import com.slack.slack.common.code.ErrorCode;
import com.slack.slack.common.dto.team.TeamChatDTO;
import com.slack.slack.common.dto.team.TeamDTO;
import com.slack.slack.common.dto.team.TeamMemberDTO;
import com.slack.slack.common.entity.*;
import com.slack.slack.common.entity.validator.TeamValidator;
import com.slack.slack.common.repository.TeamActivityRepository;
import com.slack.slack.common.repository.TeamChatRepository;
import com.slack.slack.common.repository.TeamMemberRepository;
import com.slack.slack.common.repository.TeamRepository;
import com.slack.slack.common.util.TokenManager;
import com.slack.slack.common.response.CursorResult;
import com.slack.slack.common.util.SuccessAuthentication;
import com.slack.slack.common.entity.User;
import com.slack.slack.domain.service.TeamService;
import com.slack.slack.common.repository.UserRepository;
import com.slack.slack.common.exception.*;
import com.slack.slack.common.mail.MailService;
import com.slack.slack.common.code.Key;
import com.slack.slack.common.code.State;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;

    private final UserRepository userRepository;

    private final TeamMemberRepository teamMemberRepository;

    /* 메일 서비스 */
    private final MailService mailService;

    /* 토큰 관리자 */
    private final TokenManager tokenManager;

    private final TeamValidator teamValidator;

    /**
     * 팀 생성하기
     * 팀은 1개만 생성 가능
     * */
    @Transactional
    @Override
    public Team save(TeamDTO teamDTO) {
        User user = userRepository.findByEmail(SuccessAuthentication.getPrincipal(String.class))
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        teamValidator.duplicationCheck(user);

        Team team = Team.builder()
                .name(teamDTO.getName())
                .description(teamDTO.getDescription())
                .user(user)
                .date(new Date())
                .state(State.CREATED)
                .baseCreateEntity(BaseCreateEntity.now(user.getEmail()))
                .build();

        teamRepository.save(team);

        TeamMember member = TeamMember.builder()
                .team(team)
                .user(user)
                .date(new Date())
                .baseCreateEntity(BaseCreateEntity.now(user.getEmail()))
                .build();

        member.place();

        teamMemberRepository.save(member);

        return team;
    }

    /**
     * 팀 리스트 불러오기
     * 자신의 팀과 초대된 팀을 모두 불러 옵니다.
     * */
    @Override
    @Transactional
    public List<Team> retrieveTeam() {
        User user = userRepository.findByEmail(SuccessAuthentication.getPrincipal(String.class))
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        /* 맴버 아이디 리스트에서 팀아이디를 추출합니다. */
        List<TeamMember> teamMember = teamMemberRepository.findByUser(user)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));


        /* 추출한 팀 아이디로 멤버를 불러옵니다. */
        List<Team> teams = teamRepository
                .findByTeamMemberIn(teamMember)
                .get();


        return teams.stream().map(s ->
                Team.builder()
                        .user(s.getUser())
                        .state(s.getUser() == user ? State.CREATOR : State.MEMBER)
                        .date(s.getDate())
                        .name(s.getName())
                        .description(s.getDescription())
                        .id(s.getId())
                        .teamMember(teamMember)
                        .boards(s.getBoards())
                        .build()).collect(Collectors.toList()
        );
    }

    /**
     * 팀 멤버 리스트 불러오기
     * 자신의 팀과 초대된 팀원을 모두 불러 옵니다.
     * */
    @Override
    @Transactional
    public List<TeamMember> retrieveTeamMember(Integer teamId) {
        userRepository.findByEmail(SuccessAuthentication.getPrincipal(String.class))
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        return teamMemberRepository.findByTeam(team).get();
    }

    /**
     *  팀 삭제하기
     * */
    @Override
    @Transactional
    public Team delete(TeamDTO teamDTO) {

        User user = userRepository.findByEmail(SuccessAuthentication.getPrincipal(String.class))
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Team team = teamRepository.findById(teamDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        return user.delete(team);
    }

    /**
     * 팀 정보 일부, 업데이트하기
     * */
    @Override
    @Transactional
    public Team patchUpdate(TeamDTO teamDTO) {
        teamValidator.validateTeamDTO(teamDTO);

        User user = userRepository.findByEmail(SuccessAuthentication.getPrincipal(String.class))
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Team team = teamRepository.findById(teamDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        return user.update(team, teamDTO);
    }

    /**
     * 팀 정보 업데이트하기. 전체 업데이트
     * 아이디 식별자가 있어야 합니다.
     * */
    @Override
    @Transactional
    public Team putUpdate(TeamDTO teamDTO) {
        teamValidator.validateTeamDTO(teamDTO);

        User user = userRepository.findByEmail(SuccessAuthentication.getPrincipal(String.class))
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Team team = teamRepository.findById(teamDTO.getId()).orElse(null);

        // 팀이 없으면 생성
        if (team == null)
            return this.save(teamDTO);

        // 팀이 있으면 수정
        return user.patchUpdate(team, teamDTO);
    }

    /**
     * 팀 초대하기
     * */
    @Override
    @Transactional
    public User invite(String to, TeamDTO teamDTO, Locale locale) {
        String from = SuccessAuthentication.getPrincipal(String.class);

        User user = userRepository.findByEmail(from)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));
        Team team = teamRepository.findById(teamDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));
        teamValidator.checkAuthorization(team, user);

        User invited_user = userRepository.findByEmail(to)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));
        mailService.sendInviteMail(from, to, team, locale);

        return invited_user;
    }

    /**
     * 팀 초대에 수락하기
     * */
    @Override
    @Transactional
    public TeamMember accept(String joinToken, String invitedEmail) {
        // 초대하려는 팀
        Integer teamId = Integer.parseInt(tokenManager.get(joinToken, Key.INVITE_KEY).get(1));
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        // 초대받은 유저
        User invitedUser = userRepository.findByEmail(invitedEmail)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));
        teamValidator.validateTeamAndUserForInvite(team, invitedUser);

        TeamMember teamMember = TeamMember.builder()
                .team(team)
                .user(invitedUser)
                .date(new Date())
                .baseCreateEntity(BaseCreateEntity.now(invitedUser.getEmail()))
                .build();
        teamMember.joined();

        return teamMemberRepository.save (teamMember);
    }

    /**
     * 팀원 강퇴하기
     * */
    @Override
    @Transactional
    public TeamMember kickout(TeamMemberDTO teamMemberDTO) {

        User executor = userRepository.findByEmail(SuccessAuthentication.getPrincipal(String.class))
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        // 팀이 존재하는지 확인
        teamRepository.findById(teamMemberDTO.getTeamId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        // 강퇴하려는 대상
        TeamMember member = teamMemberRepository.findById(teamMemberDTO.getId())
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        return executor.kickout(member);
    }

}
