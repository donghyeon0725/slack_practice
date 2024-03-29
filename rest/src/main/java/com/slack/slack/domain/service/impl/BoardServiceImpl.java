package com.slack.slack.domain.service.impl;

import com.slack.slack.common.code.ErrorCode;
import com.slack.slack.common.dto.board.BoardCommand;
import com.slack.slack.common.dto.board.BoardDTO;
import com.slack.slack.common.dto.team.TeamCommand;
import com.slack.slack.common.entity.*;
import com.slack.slack.common.entity.validator.BoardValidator;
import com.slack.slack.common.repository.TeamMemberRepository;
import com.slack.slack.common.repository.TeamRepository;
import com.slack.slack.common.util.SuccessAuthentication;
import com.slack.slack.common.repository.BoardRepository;
import com.slack.slack.domain.service.BoardService;
import com.slack.slack.common.entity.User;
import com.slack.slack.common.repository.UserRepository;
import com.slack.slack.common.exception.*;
import com.slack.slack.common.file.FileManager;
import com.slack.slack.common.file.FileVO;
import com.slack.slack.common.event.events.FileEvent;
import com.slack.slack.common.code.Status;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private final FileManager fileManager;

    private final ApplicationContext applicationContext;

    private final ModelMapper modelMapper;

    private final BoardValidator boardValidator;

    @Transactional
    @Override
    public Integer create(Integer teamId, BoardCommand boardCommand) {

        User user = userRepository.findByEmail(SuccessAuthentication.getPrincipal(String.class))
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        TeamMember member = teamMemberRepository.findByTeamAndUser(team, user)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        boardValidator.checkHasNoBoard(member);

        Board board = Board.builder()
                .team(team)
                .name(boardCommand.getTitle())
                .content(boardCommand.getContent())
                .date(new Date())
                .teamMember(member)
                .baseCreateEntity(BaseCreateEntity.now(user.getEmail()))
                .build();

        board.place();

        boardRepository.save(board);

        return board.getBoardId();

    }

    @Override
    @Transactional
    public Integer delete(Integer boardId) {

        boardValidator.checkValidation(boardId);

        User user = userRepository.findByEmail(SuccessAuthentication.getPrincipal(String.class))
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Team team = board.getTeam();

        TeamMember member = teamMemberRepository.findByTeamAndUser(team, user)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        member.delete(board);

        return board.getBoardId();
    }

    /**
     * 업데이트의 경우, 보드 생성자, 팀 생성자만 가능합니다.
     * */
    @Override
    @Transactional
    public Integer patchUpdate(Integer boardId, BoardCommand boardCommand) {

        boardValidator.checkValidation(boardId);

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        TeamMember member = board.getTeamMember();

        member.update(board, boardCommand);

        return board.getBoardId();
    }

    /**
     * 업데이트의 경우, 보드 생성자, 팀 생성자만 가능합니다.
     * */
    @Override
    @Transactional
    public Integer patchUpdateBanner(Integer boardId, BoardCommand boardCommand, HttpServletRequest request) {

        boardValidator.checkValidation(boardId);

        Board result = null;
        List<FileVO> files = null;
        String existingBannerPath = null;

        Board board = boardRepository.findById(boardId)
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

        return result.getBoardId();
    }


    @Override
    @Transactional
    public List<BoardDTO> retrieveBoard(TeamCommand teamCommand) {

        User user = userRepository.findByEmail(SuccessAuthentication.getPrincipal(String.class))
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Team team = teamRepository.findById(teamCommand.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        TeamMember member = teamMemberRepository.findByTeamAndUser(team, user)
                .orElseThrow(() -> new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE));


        return boardRepository
                .findByTeam(team).stream()
                .map(s-> {
                    BoardDTO returnDTO = new BoardDTO(s);
                    Status status = null;

                    if (s.getTeamMember() == member) {
                        status = Status.BOARD_CREATOR;
                    } else if (s.getTeam().getUser() == user) {
                        status = Status.CREATOR;
                    } else {
                        status = Status.NO_AUTH;
                    }
                    returnDTO.changeStatus(status);

                    return returnDTO;
                }).collect(Collectors.toList());
    }
}
