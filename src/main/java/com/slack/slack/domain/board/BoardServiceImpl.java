package com.slack.slack.domain.board;

import com.slack.slack.appConfig.security.JwtTokenProvider;
import com.slack.slack.domain.team.*;
import com.slack.slack.domain.user.User;
import com.slack.slack.domain.user.UserRepository;
import com.slack.slack.error.exception.*;
import com.slack.slack.system.Activity;
import com.slack.slack.system.State;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;

    private final UserRepository userRepository;

    private final TeamMemberRepository teamMemberRepository;

    private final TeamRepository teamRepository;

    private final TeamActivityRepository teamActivityRepository;

    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    @Override
    public Board create(String token, BoardDTO boardDTO)
            throws UserNotFoundException, ResourceNotFoundException, UnauthorizedException, ResourceConflict {

        User user = userRepository.findByEmail(jwtTokenProvider.getUserPk(token))
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        TeamMember member = teamMemberRepository.findById(boardDTO.getTeamMemberId())
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));


        Team team = teamRepository.findById(boardDTO.getTeamId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        List<Board> boards = boardRepository.findByTeamMember(member).orElse(new ArrayList<>());

        if (boards.size() > 0)
            throw new ResourceConflict(ErrorCode.RESOURCE_CONFLICT);

        if (user.getId().intValue() != member.getUser().getId())
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE);

        Board board = boardRepository.save(
                Board.builder()
                .team(team)
                .title(boardDTO.getTitle())
                .content(boardDTO.getContent())
                .date(new Date())
                .state(State.CREATED)
                .teamMember(member)
                .build()
        );

        teamActivityRepository.save(
                TeamActivity.builder()
                        .board(board)
                        .teamMember(member)
                        .detail(Activity.BOARD_CREATED)
                        .date(new Date())
                        .build()
        );

        return board;

    }

    @Override
    public Board delete(String token, BoardDTO boardDTO)
            throws UserNotFoundException, ResourceNotFoundException, UnauthorizedException {
        if (boardDTO.getId() == null)
            throw new InvalidInputException(ErrorCode.INVALID_INPUT_VALUE);

        User user = userRepository.findByEmail(jwtTokenProvider.getUserPk(token))
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        TeamMember member = teamMemberRepository.findById(boardDTO.getTeamMemberId())
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Team team = teamRepository.findById(boardDTO.getTeamId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        if (user.getId().intValue() != member.getUser().getId())
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE);


        return boardRepository.save(
                Board.builder()
                .id(boardDTO.getId())
                .team(team)
                .teamMember(member)
                .state(State.DELETED)
                .title(boardDTO.getTitle())
                .content(boardDTO.getContent())
                .build()
        );
    }

    @Override
    public Board patchUpdate(String token, BoardDTO boardDTO)
            throws UserNotFoundException, ResourceNotFoundException, UnauthorizedException, InvalidInputException {

        if (boardDTO.getId() == null || boardDTO.getTeamId() == null)
            throw new InvalidInputException(ErrorCode.INVALID_INPUT_VALUE);

        User user = userRepository.findByEmail(jwtTokenProvider.getUserPk(token))
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        boardRepository.findById(boardDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        List<TeamMember> members = teamMemberRepository.findByUser_Id(user.getId())
                .map(s->s.stream().filter(l->l.getTeam().getId().intValue() == boardDTO.getTeamId()).collect(Collectors.toList()))
                .orElse(new ArrayList<>());

        if (members.size() <= 0)
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE);

        TeamMember member = members.get(0);

        Team team = teamRepository.findById(boardDTO.getTeamId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        if (user.getId().intValue() != member.getUser().getId())
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE);


        return boardRepository.save(
                Board.builder()
                        .id(boardDTO.getId())
                        .team(team)
                        .teamMember(member)
                        .state(State.UPDATED)
                        .title(boardDTO.getTitle())
                        .content(boardDTO.getContent())
                        .build()
        );
    }

    @Override
    public List<Board> retrieveBoard(String token, TeamDTO teamDTO)
            throws UserNotFoundException, ResourceNotFoundException, UnauthorizedException {

        User user = userRepository.findByEmail(jwtTokenProvider.getUserPk(token))
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Team team = teamRepository.findById(teamDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        List<TeamMember> members = teamMemberRepository.findByUser_Id(user.getId())
                .map(s-> s.stream()
                        .filter( l -> {
                            boolean isTeamMember = l.getTeam().getId().intValue() == teamDTO.getId();
                            boolean isDeleted = l.getState().equals(State.DELETED);
                            return isTeamMember && !isDeleted;
                        }).collect(Collectors.toList()))
                .orElse(new ArrayList<>());

        if (members.size() <= 0)
            new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE);


        if (user.getId().intValue() != members.get(0).getUser().getId())
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE);

        return boardRepository.findByTeam(team).map(s->s.stream().filter(l->!l.getState().equals(State.DELETED)).collect(Collectors.toList()))
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));
    }
}
