package com.slack.slack.domain.service.impl;

import com.slack.slack.common.code.ErrorCode;
import com.slack.slack.common.code.Status;
import com.slack.slack.common.dto.team.TeamCommand;
import com.slack.slack.common.entity.*;
import com.slack.slack.common.entity.validator.TeamValidator;
import com.slack.slack.common.repository.TeamMemberRepository;
import com.slack.slack.common.repository.TeamRepository;
import com.slack.slack.common.util.TokenManager;
import com.slack.slack.common.util.SuccessAuthentication;
import com.slack.slack.common.entity.User;
import com.slack.slack.domain.service.TeamService;
import com.slack.slack.common.repository.UserRepository;
import com.slack.slack.common.exception.*;
import com.slack.slack.common.mail.MailService;
import com.slack.slack.common.code.Key;
import lombok.RequiredArgsConstructor;
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
    public Integer save(TeamCommand teamCommand) {
        User user = userRepository.findByEmail(SuccessAuthentication.getPrincipal(String.class))
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        teamValidator.checkHasNoTeam(user);

        Team team = Team.builder()
                .name(teamCommand.getName())
                .description(teamCommand.getDescription())
                .user(user)
                .date(new Date())
                .status(Status.CREATED)
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

        return team.getTeamId();
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
        List<TeamMember> teamMember = teamMemberRepository.findByUser(user);


        /* 추출한 팀 아이디로 멤버를 불러옵니다. */
        List<Team> teams = teamRepository
                .findByTeamMemberIn(teamMember);


        return teams.stream().map(s ->
                Team.builder()
                        .user(s.getUser())
                        .status(s.getUser() == user ? Status.CREATOR : Status.MEMBER)
                        .date(s.getDate())
                        .name(s.getName())
                        .description(s.getDescription())
                        .teamId(s.getTeamId())
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

        return teamMemberRepository.findByTeam(team);
    }

    /**
     *  팀 삭제하기
     * */
    @Override
    @Transactional
    public Integer delete(TeamCommand teamCommand) {

        User user = userRepository.findByEmail(SuccessAuthentication.getPrincipal(String.class))
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Team team = teamRepository.findById(teamCommand.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        user.delete(team, teamValidator);

        return team.getTeamId();
    }

    /**
     * 팀 정보 일부, 업데이트하기
     * */
    @Override
    @Transactional
    public Integer patchUpdate(Integer teamId, TeamCommand teamCommand) {
        teamValidator.checkValidation(teamCommand);

        User user = userRepository.findByEmail(SuccessAuthentication.getPrincipal(String.class))
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        user.update(team, teamCommand, teamValidator);

        return team.getTeamId();
    }

    /**
     * 팀 정보 업데이트하기. 전체 업데이트
     * 아이디 식별자가 있어야 합니다.
     * */
    @Override
    @Transactional
    public Integer putUpdate(Integer teamId, TeamCommand teamCommand) {
        teamValidator.checkValidation(teamCommand);

        User user = userRepository.findByEmail(SuccessAuthentication.getPrincipal(String.class))
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Team team = teamRepository.findById(teamId).orElse(null);

        // 팀이 없으면 생성
        if (team == null)
            return this.save(teamCommand);

        user.patchUpdate(team, teamCommand, teamValidator);

        // 팀이 있으면 수정
        return team.getTeamId();
    }

    /**
     * 팀 초대하기
     * */
    @Override
    @Transactional
    public Integer invite(Integer teamId, String to, Locale locale) {
        String from = SuccessAuthentication.getPrincipal(String.class);

        User user = userRepository.findByEmail(from)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));
        teamValidator.checkTeamOwner(team, user);

        User invited_user = userRepository.findByEmail(to)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));
        mailService.sendInviteMail(from, to, team, locale);

        return invited_user.getUserId();
    }

    /**
     * 팀 초대에 수락하기
     * */
    @Override
    @Transactional
    public Integer accept(String joinToken, String invitedEmail) {
        // 초대하려는 팀
        Integer teamId = Integer.parseInt(tokenManager.get(joinToken, Key.INVITE_KEY).get(1));
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        // 초대받은 유저
        User invitedUser = userRepository.findByEmail(invitedEmail)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));
        teamValidator.checkAlreadyIsTeamMember(team, invitedUser);

        TeamMember teamMember = TeamMember.builder()
                .team(team)
                .user(invitedUser)
                .date(new Date())
                .baseCreateEntity(BaseCreateEntity.now(invitedUser.getEmail()))
                .build();
        teamMember.joined();

        teamMemberRepository.save(teamMember);

        return teamMember.getTeamMemberId();
    }

    /**
     * 팀원 강퇴하기
     * */
    @Override
    @Transactional
    public Integer kickout(Integer teamId, Integer teamMemberId) {

        User executor = userRepository.findByEmail(SuccessAuthentication.getPrincipal(String.class))
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        // 팀이 존재하는지 확인
        teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        // 강퇴하려는 대상
        TeamMember member = teamMemberRepository.findById(teamMemberId)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        executor.kickout(member);

        return member.getTeamMemberId();
    }

}
