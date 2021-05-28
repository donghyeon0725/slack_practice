package com.slack.slack.domain.team;

import com.slack.slack.appConfig.security.JwtTokenProvider;
import com.slack.slack.appConfig.security.TokenManager;
import com.slack.slack.domain.user.User;
import com.slack.slack.domain.user.UserDTO;
import com.slack.slack.domain.user.UserRepository;
import com.slack.slack.error.exception.*;
import com.slack.slack.mail.MailService;
import com.slack.slack.system.Activity;
import com.slack.slack.system.Key;
import com.slack.slack.system.Role;
import com.slack.slack.system.State;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import javax.validation.constraints.Past;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;

    private final UserRepository userRepository;

    private final TeamMemberRepository teamMemberRepository;

    private final TeamActivityRepository teamActivityRepository;

    /* 메일 서비스 */
    private final MailService mailService;

    /* 토큰 확인을 위한 모듈 */
    private final JwtTokenProvider jwtTokenProvider;

    /* 토큰 관리자 */
    private final TokenManager tokenManager;

    /**
     * 팀 생성하기
     * 팀은 1개만 생성 가능
     *
     * @ param String token 토큰
     * @ param TeamDTO teamDTO 팀 정보
     * @ exception UserNotFoundException : 사용자가 검색되지 않을 경우 반환합니다.
     * @ exception ResourceConflict : 이미 자원이 존재하는 경우 반환합니다.
     * */
    @Transactional
    @Override
    public Team save(String token, TeamDTO teamDTO) throws UserNotFoundException, ResourceConflict {
        User user = userRepository.findByEmail(jwtTokenProvider.getUserPk(token))
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        teamRepository.findByUserId(user.getId()).ifPresent(
            team -> {
                throw new ResourceConflict(ErrorCode.RESOURCE_CONFLICT);
            }
        );


        Team savedTeam = teamRepository.save(
                Team.builder()
                .name(teamDTO.getName())
                .description(teamDTO.getDescription())
                .user(user)
                .date(new Date())
                .state(State.CREATED)
                .build()
        );

        TeamMember member = teamMemberRepository.save(
                TeamMember.builder()
                .team(savedTeam)
                .user(user)
                .date(new Date())
                .build()
        );

        TeamActivity teamActivity = teamActivityRepository.save(
                TeamActivity.builder()
                .teamMember(member)
                .detail(Activity.TEAM_CREATED)
                .date(new Date())
                .build()
        );

        return savedTeam;
    }

    /**
     * 팀 리스트 불러오기
     * 자신의 팀과 초대된 팀을 모두 불러 옵니다.
     *
     * @ param String token 토큰
     * @ param TeamDTO teamDTO 팀 정보
     * @ exception UserNotFoundException : 사용자가 검색되지 않을 경우 반환합니다.
     * */
    @Override
    public List<Team> retrieveTeam(String token) throws UserNotFoundException {
        /* 맴버 아이디 리스트에서 팀아이디를 추출합니다. */
        List<TeamMember> teamMember = teamMemberRepository.findByUser_Id(
                        /* 멤버 아이디를 불러옵니다. */
                        userRepository
                                .findByEmail(jwtTokenProvider.getUserPk(token))
                                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND))
                                .getId())
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));


        /* 추출한 팀 아이디로 멤버를 불러옵니다. */
        return teamRepository
                .findByTeamMemberIn(teamMember)
                // 삭제 되지 않은 것만 필터
                .map(l -> l.stream()
                        .filter(t -> t.getState() != State.DELETED).collect(Collectors.toList()))
                .orElse(null);
    }

    /**
     *  팀 삭제하기
     *
     * @ param String token 토큰
     * @ param TeamDTO teamDTO 팀 정보
     * @ exception ResourceNotFoundException : 팀이 검색되지 않을 경우 반환합니다.
     * @ exception UserNotFoundException : 사용자가 검색되지 않을 경우 반환합니다.
     * @ exception UnauthorizedException : 권한이 없는 자원입니다.
     * */
    @Override
    public Team delete(String token, TeamDTO teamDTO) throws UserNotFoundException, ResourceNotFoundException, UnauthorizedException {

        User user = userRepository.findByEmail(jwtTokenProvider.getUserPk(token))
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Team team = teamRepository.findById(teamDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        if (user.getId() != team.getUser().getId().intValue())
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE);

        // 상태를 삭제로 변경
        return teamRepository.save(
                Team.builder()
                        .id(team.getId())
                        .user(team.getUser())
                        .name(team.getName())
                        .description(team.getDescription())
                        .state(State.DELETED)
                        .date(team.getDate())
                        .build()
        );
    }

    /**
     * 팀 정보 일부, 업데이트하기
     *
     * @ param String token 토큰
     * @ param TeamDTO teamDTO 팀 정보
     * @ exception ResourceNotFoundException : 팀이 검색되지 않을 경우 반환합니다.
     * @ exception UserNotFoundException : 사용자가 검색되지 않을 경우 반환합니다.
     * @ exception UnauthorizedException : 권한이 없는 자원입니다.
     * @ exception InvalidInputException : 아이디가 없거나, 값이 잘못된 경우 반환합니다.
     * */
    @Override
    public Team patchUpdate(String token, TeamDTO teamDTO) throws ResourceNotFoundException, UserNotFoundException, UnauthorizedException, InvalidInputException {
        if (teamDTO.getId() == null)
            throw new InvalidInputException(ErrorCode.INVALID_INPUT_VALUE);

        User user = userRepository.findByEmail(jwtTokenProvider.getUserPk(token))
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Team team = teamRepository.findById(teamDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        if (team.getState() == State.DELETED)
            throw new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND);

        if (user.getId() != team.getUser().getId().intValue())
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE);

        // 상태를 삭제로 변경
        return teamRepository.save(
                Team.builder()
                        .id(team.getId())
                        .user(team.getUser())
                        .name(teamDTO.getName())
                        .description(teamDTO.getDescription())
                        .state(State.UPDATED)
                        .date(team.getDate())
                        .build()
        );
    }

    /**
     * 팀 정보 업데이트하기. 전체 업데이트
     * 아이디 식별자가 있어야 합니다.
     *
     * @ param String token 토큰
     * @ param TeamDTO teamDTO 팀 정보
     * @ exception UserNotFoundException : 유저가 없을 경우 반환합니다.
     * @ exception ResourceNotFoundException : 팀이 검색되지 않았거나 삭제되었을 경우 반환합니다.
     * @ exception UnauthorizedException : 권한이 없는 자원입니다.
     * @ exception InvalidInputException : 아이디가 없거나, 값이 잘못된 경우 반환합니다.
     * @ exception ResourceConflict : 이미 팀이 있는 경우 반환합니다.
     * */
    @Override
    public Team putUpdate(String token, TeamDTO teamDTO) throws ResourceNotFoundException, UserNotFoundException, UnauthorizedException, InvalidInputException, ResourceConflict {

        if (teamDTO.getId() == null)
            throw new InvalidInputException(ErrorCode.INVALID_INPUT_VALUE);

        User user = userRepository.findByEmail(jwtTokenProvider.getUserPk(token))
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        /* 팀을 자신의 아이디로 삭제되지 않은 팀을 조회 합니다. */
        List<Team> teams = this.retrieveTeam(token);

        /* 나의 팀 */
        List<Team> teamCreatedByMe = teams.stream().filter(s->s.getUser().getId() == user.getId()).collect(Collectors.toList());

        /* 또 자신이 생성한 팀이 조회되지 않을 때 팀을 생성합니다. */
        boolean existMyTeam = teamCreatedByMe.size() > 0;
        if (!existMyTeam) return this.save(token, teamDTO);

        /* 수정하려는 팀의 아이디와 내가 생성한 팀의 아이디 비교. 수정하려는 팀의 아이디가 내 팀이 아니라면 수정할 수 없습니다. */
        List<Team> canModifyTeams = teamCreatedByMe.stream().filter(s->s.getId().intValue() == teamDTO.getId().intValue()).collect(Collectors.toList());

        /* 수정할 수 있는 팀이 없습니다. */
        if (canModifyTeams.size() <= 0)
            throw new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND);

        return this.patchUpdate(token, teamDTO);
    }

    /**
     * 팀 초대하기
     *
     * @ param String token 토큰
     * @ param TeamDTO teamDTO 팀 정보
     * @ exception UnauthorizedException : 팀 생성자가 아닐 경우 반환 합니다.
     * @ exception ResourceNotFoundException : 팀 생성자가 아닙니다.
     * */
    @Override
    public User invite(String token, String to, TeamDTO teamDTO, Locale locale) throws UnauthorizedException, ResourceNotFoundException {
        String from = jwtTokenProvider.getUserPk(token);

        User user = userRepository.findByEmail(from)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));


        User invited_user = userRepository.findByEmail(to)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        if (teamDTO.getId() == user.getId().intValue())
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE);

        Team team = teamRepository.findById(teamDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        mailService.sendInviteMail(from, to, team, locale);

        return invited_user;
    }

    /**
     * 팀 초대에 수락하기
     *
     * @ param String token 토큰
     * @ param TeamDTO teamDTO 팀 정보
     * @ exception InvalidTokenException : 토큰이 잘못되었을 경우 반환합니다.
     * @ exception ResourceNotFoundException : 팀이 검색되지 않을 경우 반환합니다.
     * @ exception UserNotFoundException : 초대 이메일이 유효하지 않거나 없을 때 경우 반환합니다.
     * */
    @Override
    public TeamMember accept(String joinToken, String email) throws InvalidTokenException, ResourceNotFoundException, UserNotFoundException {

        String from = tokenManager.get(joinToken, Key.INVITE_KEY).get(0);

        Integer teamId = Integer.parseInt(tokenManager.get(joinToken, Key.INVITE_KEY).get(1));

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        // 초대받은 사람이 이미 유저로 있으면
        List<TeamMember> members = teamMemberRepository.findByUser_Id(user.getId())
                .map( s -> s.stream()
                        // 리스트에서 팀 아이디가 같은지 추출
                        .filter(l -> l.getTeam().getId().intValue() == team.getId())
                        .collect(Collectors.toList()))
                .orElse(new ArrayList<>());

        if (members.size() > 0) {
            throw new ResourceConflict(ErrorCode.RESOURCE_CONFLICT);
        }

        if (!tokenManager.isInvalid(joinToken, Key.INVITE_KEY))
            throw new InvalidTokenException(ErrorCode.INVALID_INPUT_VALUE);

        TeamMember member = TeamMember.builder()
                .team(team)
                .user(user)
                .state(State.JOIN)
                .date(new Date())
                .build();


        return teamMemberRepository.save(member);
    }

    /**
     * 팀원 강퇴하기
     *
     * @ param String token 토큰
     * @ param TeamMemberDTO teamMemberDTO 팀원의 정보
     *
     * @ exception InvalidTokenException : 토큰이 잘못되었을 경우 반환합니다.
     * @ exception ResourceNotFoundException : 팀이 검색되지 않을 경우 반환합니다.
     * @ exception UserNotFoundException : 팀 생성자나, 멤버가 검색되지 않을 경우 반환합니다.
     * @ exception UnauthorizedException : 팀에 대한 권한이 없을 경우 반환합니다.
     * */
    @Override
    public TeamMember kickout(String token, TeamMemberDTO teamMemberDTO) {
        User teamCreator = userRepository.findByEmail(jwtTokenProvider.getUserPk(token))
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Team team = teamRepository.findById(teamMemberDTO.getTeamId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        if (teamCreator.getId() != team.getUser().getId())
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE);

        if (teamCreator.getId() == teamMemberDTO.getUserId().intValue())
            throw new InvalidInputException(ErrorCode.INVALID_INPUT_VALUE);

        TeamMember member = teamMemberRepository.findById(teamMemberDTO.getId())
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        return teamMemberRepository.save(
                TeamMember.builder()
                        .id(member.getId())
                        .team(member.getTeam())
                        .user(member.getUser())
                        .date(member.getDate())
                        .state(State.KICKOUT)
                        .build());
    }
}
