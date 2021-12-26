package com.slack.slack.common.entity.validator;

import com.slack.slack.common.code.ErrorCode;
import com.slack.slack.common.dto.board.BoardDTO;
import com.slack.slack.common.entity.TeamMember;
import com.slack.slack.common.exception.InvalidInputException;
import com.slack.slack.common.exception.ResourceConflict;
import com.slack.slack.common.exception.UnauthorizedException;
import com.slack.slack.common.repository.BoardRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BoardValidator {

    private final BoardRepository boardRepository;

    public void checkHasTeamMemberNoBoard(TeamMember teamMember) {

        if (boardRepository.findByTeamMember(teamMember).get().size() > 0)
            throw new ResourceConflict(ErrorCode.RESOURCE_CONFLICT);

    }

    public void checkValidation(BoardDTO boardDTO) {
        if (boardDTO.getId() == null)
            throw new InvalidInputException(ErrorCode.INVALID_INPUT_VALUE);
    }
}
