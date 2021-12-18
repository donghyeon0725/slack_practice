package com.slack.slack.domain.service.impl;

import com.slack.slack.common.code.ErrorCode;
import com.slack.slack.common.dto.board.BoardDTO;
import com.slack.slack.common.dto.board.BoardReturnDTO;
import com.slack.slack.common.dto.team.TeamDTO;
import com.slack.slack.common.entity.*;
import com.slack.slack.common.entity.validator.BoardValidator;
import com.slack.slack.common.repository.TeamActivityRepository;
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
import com.slack.slack.common.code.Activity;
import com.slack.slack.common.code.State;
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
    public Integer create(BoardDTO boardDTO) {

        User user = userRepository.findByEmail(SuccessAuthentication.getPrincipal(String.class))
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Team team = teamRepository.findById(boardDTO.getTeamId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        TeamMember member = teamMemberRepository.findByTeamAndUser(team, user)
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        boardValidator.validateForCreateBoard(member);

        Board board = Board.builder()
                .team(team)
                .title(boardDTO.getTitle())
                .content(boardDTO.getContent())
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
    public Integer delete(BoardDTO boardDTO) {

        boardValidator.validateBoardDTOForUpdate(boardDTO);

        User user = userRepository.findByEmail(SuccessAuthentication.getPrincipal(String.class))
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Board board = boardRepository.findById(boardDTO.getId())
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
    public Integer patchUpdate(BoardDTO boardDTO) {

        boardValidator.validateBoardDTOForUpdate(boardDTO);

        Board board = boardRepository.findById(boardDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        TeamMember member = board.getTeamMember();

        member.update(board, boardDTO);

        return board.getBoardId();
    }

    /**
     * 업데이트의 경우, 보드 생성자, 팀 생성자만 가능합니다.
     * */
    @Override
    @Transactional
    public Integer patchUpdateBanner(HttpServletRequest request, BoardDTO boardDTO) {

        boardValidator.validateBoardDTOForUpdate(boardDTO);

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

        return result.getBoardId();
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
