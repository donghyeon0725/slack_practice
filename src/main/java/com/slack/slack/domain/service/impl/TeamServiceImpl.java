package com.slack.slack.domain.service.impl;

import com.slack.slack.common.code.ErrorCode;
import com.slack.slack.common.dto.team.TeamChatDTO;
import com.slack.slack.common.dto.team.TeamDTO;
import com.slack.slack.common.dto.team.TeamMemberDTO;
import com.slack.slack.common.entity.*;
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
import com.slack.slack.error.exception.*;
import com.slack.slack.listener.event.chat.TeamChatAddEvent;
import com.slack.slack.listener.event.chat.TeamChatUpdateEvent;
import com.slack.slack.mail.MailService;
import com.slack.slack.common.code.Activity;
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

    private final TeamActivityRepository teamActivityRepository;

    private final TeamChatRepository teamChatRepository;

    private final ApplicationContext applicationContext;

    /* 메일 서비스 */
    private final MailService mailService;

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
    public Team save(TeamDTO teamDTO) throws UserNotFoundException, ResourceConflict {
        User user = userRepository.findByEmail(SuccessAuthentication.getPrincipal(String.class))
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Team.duplicationCheck(user, teamRepository);

        Team savedTeam = teamRepository.save(
                Team.builder()
                        .name(teamDTO.getName())
                        .description(teamDTO.getDescription())
                        .user(user)
                        .date(new Date())
                        .state(State.CREATED)
                        .baseCreateEntity(BaseCreateEntity.now(user.getEmail()))
                        .build()
        );

        TeamMember member = teamMemberRepository.save(
                TeamMember.builder()
                        .team(savedTeam)
                        .user(user)
                        .state(State.CREATED)
                        .date(new Date())
                        .baseCreateEntity(BaseCreateEntity.now(user.getEmail()))
                        .build()
        );

        teamActivityRepository.save(
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
     * @ param TeamDTO teamDTO 팀 정보
     * @ exception UserNotFoundException : 사용자가 검색되지 않을 경우 반환합니다.
     * */
    @Override
    @Transactional
    public List<Team> retrieveTeam() throws UserNotFoundException {
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
     *
     * @ param String token 토큰
     * @ param TeamDTO teamDTO 팀 정보
     * @ exception UnauthorizedException : 사용자가 권한이 없을 때 반환합니다.
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
     *
     * @ param String token 토큰
     * @ param TeamDTO teamDTO 팀 정보
     * @ exception ResourceNotFoundException : 팀이 검색되지 않을 경우 반환합니다.
     * @ exception UserNotFoundException : 사용자가 검색되지 않을 경우 반환합니다.
     * @ exception UnauthorizedException : 권한이 없는 자원입니다.
     * */
    @Override
    @Transactional
    public Team delete(TeamDTO teamDTO) throws UserNotFoundException, ResourceNotFoundException, UnauthorizedException {

        User user = userRepository.findByEmail(SuccessAuthentication.getPrincipal(String.class))
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Team team = teamRepository.findById(teamDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));


        return user.delete(team);
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
    @Transactional
    public Team patchUpdate(TeamDTO teamDTO) throws ResourceNotFoundException, UserNotFoundException, UnauthorizedException, InvalidInputException {
        teamDTO.checkValidation();

        User user = userRepository.findByEmail(SuccessAuthentication.getPrincipal(String.class))
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Team team = teamRepository.findById(teamDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        return user.update(team, teamDTO);
    }

    /**
     * 팀 정보 업데이트하기. 전체 업데이트
     * 아이디 식별자가 있어야 합니다.
     *
     * @ param String token 토큰
     * @ param TeamDTO teamDTO 팀 정보
     * @ exception UserNotFoundException : 유저가 없을 경우 반환합니다.
     * @ exception ResourceNotFoundException : 팀이 검색되지 않았거나 삭제되었을 경우 반환합니다.
     * @ exception InvalidInputException : 아이디가 없거나, 값이 잘못된 경우 반환합니다.
     * @ exception UnauthorizedException : 권한이 없는 자원입니다.
     * */
    @Override
    @Transactional
    public Team putUpdate(TeamDTO teamDTO) throws ResourceNotFoundException, UserNotFoundException, InvalidInputException,UnauthorizedException {
        teamDTO.checkValidation();

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
     *
     * @ param String token 토큰
     * @ param TeamDTO teamDTO 팀 정보
     * @ exception UnauthorizedException : 팀 생성자가 아닐 경우 반환 합니다.
     * @ exception ResourceNotFoundException : 팀 생성자가 아닙니다.
     * @ exception UserNotFoundException : 유저가 없습니다.
     * */
    @Override
    @Transactional
    public User invite(String to, TeamDTO teamDTO, Locale locale) throws UserNotFoundException, UnauthorizedException, ResourceNotFoundException {
        String from = SuccessAuthentication.getPrincipal(String.class);

        User user = userRepository.findByEmail(from)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Team team = teamRepository.findById(teamDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        if (team.getUser() != user)
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE);

        User invited_user = userRepository.findByEmail(to)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

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
    @Transactional
    public TeamMember accept(String joinToken, String invitedEmail) throws InvalidTokenException, ResourceNotFoundException, UserNotFoundException {
        // 초대하려는 팀
        Integer teamId = Integer.parseInt(tokenManager.get(joinToken, Key.INVITE_KEY).get(1));
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        // 초대받은 유저
        User invitedUser = userRepository.findByEmail(invitedEmail)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        // 이미 있을시 예외
        teamMemberRepository.findByTeamAndUser(team, invitedUser).ifPresent(teamMember -> {
            throw new ResourceConflict(ErrorCode.RESOURCE_CONFLICT);
        });

        // 저장
        return teamMemberRepository.save (
                TeamMember.builder()
                        .team(team)
                        .user(invitedUser)
                        .state(State.JOIN)
                        .date(new Date())
                        .baseCreateEntity(BaseCreateEntity.now(invitedUser.getEmail()))
                        .build()
        );
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
    @Transactional
    public TeamMember kickout(TeamMemberDTO teamMemberDTO)
            throws  UnauthorizedException, UserNotFoundException, ResourceNotFoundException, InvalidTokenException {

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


    CursorResult<Board> getChat(Integer teamId, Integer chatId, Pageable page) {
        final List<TeamChat> chats = retrieveTeamChat(teamId, chatId, page);
        final Integer lastIdOfList = chats.isEmpty() ?
                null : chats.get(chats.size() - 1).getId();

        return new CursorResult(chats, hasNext(lastIdOfList));
    }

    private Boolean hasNext(Integer id) {
        if (id == null) return false;
        return teamChatRepository.existsByIdLessThan(id);
    }

    @Transactional
    @Override
    public List<TeamChat> retrieveTeamChat(Integer teamId, Integer chatId, Pageable page) {
        return chatId == null ?
                teamChatRepository.findAllByTeamOrderByIdDesc(teamId, page).stream().sorted(Comparator.comparingInt(TeamChat::getId)).collect(Collectors.toList()) :
                teamChatRepository.findByTeamWhereIdLessThanOrderByIdDesc(teamId, chatId, page).stream().sorted(Comparator.comparingInt(TeamChat::getId)).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public TeamChat deleteTeamChat(TeamChatDTO teamChatDTO) {
        TeamChat teamChat = teamChatRepository.findById(teamChatDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        TeamChat chat = teamChat.delete();

        applicationContext.publishEvent(new TeamChatUpdateEvent(TeamChat.builder()
                .id(chat.getId())
                .email(chat.getEmail())
                .date(chat.getDate())
                // 삭제 메세지로 return
                .description(State.DELETED.getDescription())
                .team(chat.getTeam())
                .user(chat.getUser())
                .state(chat.getState())
                .build())
        );

        return chat;
    }

    @Transactional
    @Override
    public TeamChat createTeamChat(TeamChatDTO teamChatDTO) {
        User user = userRepository.findByEmail(SuccessAuthentication.getPrincipal(String.class))
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Team team = teamRepository.findById(teamChatDTO.getTeamId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));


        TeamChat chat = teamChatRepository.save(
                TeamChat.builder()
                        .email(user.getEmail())
                        .date(new Date())
                        .description(teamChatDTO.getDescription())
                        .team(team)
                        .user(user)
                        .state(State.CREATED)
                        .baseCreateEntity(BaseCreateEntity.now(user.getEmail()))
                        .build()
        );

        applicationContext.publishEvent(new TeamChatAddEvent(chat));

        return chat;
    }
}
