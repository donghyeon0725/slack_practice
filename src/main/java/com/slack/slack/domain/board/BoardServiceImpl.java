package com.slack.slack.domain.board;

import com.slack.slack.appConfig.security.JwtTokenProvider;
import com.slack.slack.domain.common.BaseCreateEntity;
import com.slack.slack.domain.common.BaseModifyEntity;
import com.slack.slack.domain.common.SuccessAuthentication;
import com.slack.slack.domain.team.*;
import com.slack.slack.domain.user.User;
import com.slack.slack.domain.user.UserRepository;
import com.slack.slack.error.exception.*;
import com.slack.slack.file.FileManager;
import com.slack.slack.file.FileVO;
import com.slack.slack.listener.event.file.FileEvent;
import com.slack.slack.system.Activity;
import com.slack.slack.system.State;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
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

    private final FileManager fileManager;

    private final ApplicationContext applicationContext;

    private final ModelMapper modelMapper;

    @Transactional
    @Override
    public Board create(BoardDTO boardDTO)
            throws UserNotFoundException, ResourceNotFoundException, UnauthorizedException, ResourceConflict {

        User user = userRepository.findByEmail(SuccessAuthentication.getPrincipal(String.class))
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Team team = teamRepository.findById(boardDTO.getTeamId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        TeamMember member = teamMemberRepository.findByTeamAndUser(team, user)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        if (boardRepository.findByTeamMember(member).get().size() > 0)
            throw new ResourceConflict(ErrorCode.RESOURCE_CONFLICT);

        if (user != member.getUser())
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE);

        Board board = boardRepository.save(
                Board.builder()
                .team(team)
                .title(boardDTO.getTitle())
                .content(boardDTO.getContent())
                .date(new Date())
                .state(State.CREATED)
                .teamMember(member)
                .baseCreateEntity(BaseCreateEntity.now(user.getEmail()))
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
    @Transactional
    public Board delete(BoardDTO boardDTO)
            throws UserNotFoundException, ResourceNotFoundException, UnauthorizedException {

        boardDTO.checkValidation();


        User user = userRepository.findByEmail(SuccessAuthentication.getPrincipal(String.class))
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Board board = boardRepository.findById(boardDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Team team = board.getTeam();

        TeamMember member = teamMemberRepository.findByTeamAndUser(team, user)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        return member.delete(board);
    }

    /**
     * 업데이트의 경우, 보드 생성자, 팀 생성자만 가능합니다.
     * */
    @Override
    @Transactional
    public Board patchUpdate(BoardDTO boardDTO)
            throws UserNotFoundException, ResourceNotFoundException, UnauthorizedException, InvalidInputException {

        boardDTO.checkValidation();

        Board board = boardRepository.findById(boardDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        TeamMember member = board.getTeamMember();

        return member.update(board, boardDTO);
    }

    /**
     * 업데이트의 경우, 보드 생성자, 팀 생성자만 가능합니다.
     * */
    @Override
    @Transactional
    public Board patchUpdateBanner(HttpServletRequest request, BoardDTO boardDTO)
            throws UserNotFoundException, ResourceNotFoundException, UnauthorizedException, InvalidInputException {

        boardDTO.checkValidation();

        Board result = null;
        List<FileVO> files = null;
        String existingBannerPath = null;


        Board board = boardRepository.findById(boardDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        TeamMember member = board.getTeamMember();



        try {
            // 기존 배너가 있었는지 check
            if (!board.isBannerEmpty()) {
                existingBannerPath = board.getBannerPath();
            }

            files = fileManager.fileUpload(request);
            result = member.updateBanner(board, files.get(0).getAbsolutePath());

            if (existingBannerPath != null)
                fileManager.deleteFile(Arrays.asList(FileVO.builder().absolutePath(existingBannerPath).build()));

        } catch (RuntimeException e) {
            applicationContext.publishEvent(new FileEvent(files));
        }

        return result;
    }


    @Override
    @Transactional
    public List<BoardReturnDTO> retrieveBoard(TeamDTO teamDTO)
            throws UserNotFoundException, ResourceNotFoundException, UnauthorizedException {

        User user = userRepository.findByEmail(SuccessAuthentication.getPrincipal(String.class))
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Team team = teamRepository.findById(teamDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        TeamMember member = teamMemberRepository.findByTeamAndUser(team, user)
                .orElseThrow(() -> new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE));


        return boardRepository
                .findByTeam(team).get().stream()
                .map(s-> {
                    State state = null;

                    if (s.getTeamMember() == member) {
                        state = State.BOARD_CREATOR;
                    } else if (s.getTeam().getUser() == user) {
                        state = State.CREATOR;
                    } else {
                        state = State.NO_AUTH;
                    }

                    BoardReturnDTO returnDTO = modelMapper.map(s, BoardReturnDTO.class);
                    returnDTO.changeState(state);

                    return returnDTO;
                }).collect(Collectors.toList());
    }
}
