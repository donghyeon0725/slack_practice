package com.slack.slack.domain.board;

import com.slack.slack.appConfig.security.JwtTokenProvider;
import com.slack.slack.domain.team.*;
import com.slack.slack.domain.user.User;
import com.slack.slack.domain.user.UserRepository;
import com.slack.slack.error.exception.*;
import com.slack.slack.event.FileEvent;
import com.slack.slack.file.FileManager;
import com.slack.slack.file.FileVO;
import com.slack.slack.system.Activity;
import com.slack.slack.system.State;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;
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

    private final FileManager fileManager;

    private final ApplicationContext applicationContext;

    @Transactional
    @Override
    public Board create(String token, BoardDTO boardDTO)
            throws UserNotFoundException, ResourceNotFoundException, UnauthorizedException, ResourceConflict {

        User user = userRepository.findByEmail(jwtTokenProvider.getUserPk(token))
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Team team = teamRepository.findById(boardDTO.getTeamId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        TeamMember member = teamMemberRepository.findByTeamAndUser(team, user)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        List<Board> boards = boardRepository.findByTeamMember(member).orElse(new ArrayList<>());

        System.out.println(boards.size());
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

        Board board = boardRepository.findById(boardDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Team team = board.getTeam();

        TeamMember member = teamMemberRepository.findByTeamAndUser(team, user)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        if (user.getId().intValue() != member.getUser().getId())
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE);


        return boardRepository.save(
                Board.builder()
                .id(board.getId())
                .team(team)
                .teamMember(member)
                .state(State.DELETED)
                .title(board.getTitle())
                .content(board.getContent())
                .build()
        );
    }

    /**
     * 업데이트의 경우, 보드 생성자, 팀 생성자만 가능합니다.
     * */
    @Override
    public Board patchUpdate(String token, BoardDTO boardDTO)
            throws UserNotFoundException, ResourceNotFoundException, UnauthorizedException, InvalidInputException {

        if (boardDTO.getId() == null)
            throw new InvalidInputException(ErrorCode.INVALID_INPUT_VALUE);

        User user = userRepository.findByEmail(jwtTokenProvider.getUserPk(token))
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Board board = boardRepository.findById(boardDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Team team = board.getTeam();

        TeamMember member = board.getTeamMember();

        User teamCreator = team.getUser();

        User boardCreator = board.getTeamMember().getUser();

        /*List<TeamMember> members = teamMemberRepository.findByUser_Id(user.getId())
                .map(s->s.stream().filter(l->l.getTeam().getId().intValue() == boardDTO.getTeamId()).collect(Collectors.toList()))
                .orElse(new ArrayList<>());*/

        if (user.getId().intValue() != teamCreator.getId() && user.getId().intValue() != boardCreator.getId())
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE);

        return boardRepository.save(
                Board.builder()
                        .id(boardDTO.getId())
                        .team(team)
                        .teamMember(member)
                        .state(State.UPDATED)
                        .bannerPath(board.getBannerPath())
                        .title(boardDTO.getTitle())
                        .content(boardDTO.getContent())
                        .build()
        );
    }

    /**
     * 업데이트의 경우, 보드 생성자, 팀 생성자만 가능합니다.
     * */
    @Override
    public Board patchUpdateBanner(HttpServletRequest request, String token, BoardDTO boardDTO)
            throws UserNotFoundException, ResourceNotFoundException, UnauthorizedException, InvalidInputException {

        Board result = null;

        if (boardDTO.getId() == null)
            throw new InvalidInputException(ErrorCode.INVALID_INPUT_VALUE);

        User user = userRepository.findByEmail(jwtTokenProvider.getUserPk(token))
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Board board = boardRepository.findById(boardDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Team team = board.getTeam();

        TeamMember member = board.getTeamMember();

        User teamCreator = team.getUser();

        User boardCreator = board.getTeamMember().getUser();

        if (user.getId().intValue() != teamCreator.getId() && user.getId().intValue() != boardCreator.getId())
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE);

        List<FileVO> files = null;
        try {

            String existingBannerPath = null;
            if (!board.isBannerEmpty()) {
                existingBannerPath = board.getBannerPath();
            }

            files = fileManager.fileUpload(request);

            if (files != null && files.size() > 0) {
                result = boardRepository.save(
                        Board.builder()
                                .id(board.getId())
                                .team(team)
                                .teamMember(member)
                                .state(State.UPDATED)
                                .title(board.getTitle())
                                .content(board.getContent())
                                .bannerPath(files.get(0).getAbsolutePath())
                                .build()
                );
            }

            // 기존 파일 제거
            fileManager.deleteFile(Arrays.asList(FileVO.builder().absolutePath(existingBannerPath).build()));
        } catch (RuntimeException e) {
            applicationContext.publishEvent(new FileEvent(files));
        }

        return result;
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

        TeamMember member = teamMemberRepository.findByTeamAndUser(team, user)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));
        int final_memberId = member.getId();


        return boardRepository
                .findByTeam(team)
                .map(s->s.stream().filter(l->!l.getState().equals(State.DELETED)).collect(Collectors.toList()))
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND))
                .stream()
                .map(s-> {
                    State state = null;

                    if (s.getTeamMember().getId() == final_memberId) {
                        state = State.BOARD_CREATOR;
                    } else if (s.getTeam().getUser().getId() == user.getId()) {
                        state = State.CREATOR;
                    } else {
                        state = State.NO_AUTH;
                    }

                    return Board.builder()
                            .id(s.getId())
                            .content(s.getContent())
                            .team(s.getTeam())
                            .teamMember(s.getTeamMember())
                            .bannerPath(s.getBannerPath())
                            .title(s.getTitle())
                            .date(s.getDate())
                            .state(state)
                            .build();
                }).collect(Collectors.toList());
    }
}
