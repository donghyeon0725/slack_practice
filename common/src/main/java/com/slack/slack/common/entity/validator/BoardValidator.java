package com.slack.slack.common.entity.validator;

import com.slack.slack.common.code.ErrorCode;
import com.slack.slack.common.entity.TeamMember;
import com.slack.slack.common.exception.InvalidInputException;
import com.slack.slack.common.exception.ResourceConflict;
import com.slack.slack.common.repository.BoardRepository;
import com.slack.slack.common.repository.TeamMemberRepository;
import org.springframework.stereotype.Component;

@Component
public class BoardValidator extends PermissionValidator{

    private final BoardRepository boardRepository;

    public BoardValidator(TeamMemberRepository teamMemberRepository, BoardRepository boardRepository) {
        super(teamMemberRepository);
        this.boardRepository = boardRepository;
    }

    public void checkHasNoBoard(TeamMember teamMember) {

        if (boardRepository.findByTeamMember(teamMember).size() > 0)
            throw new ResourceConflict(ErrorCode.RESOURCE_CONFLICT);

    }

    public void checkValidation(Integer boardId) {
        if (boardId == null)
            throw new InvalidInputException(ErrorCode.INVALID_INPUT_VALUE);
    }

//    public void checkTeamOwnerOrBoardOwner(Board board, User user) {
//
//        Team team = board.getTeam();
//
//        TeamMember teamMember = teamMemberRepository.findByTeamAndUser(team, user)
//                .orElseThrow(() -> new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE));
//
//        boolean isBoardOwner = board.getTeamMember().getTeamMemberId().equals(teamMember.getTeamMemberId());
//
//        boolean isTeamOwner = team.getUser().getUserId().equals(user.getUserId());
//
//        if (!isBoardOwner && !isTeamOwner)
//            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE);
//    }
}
