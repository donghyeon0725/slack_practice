package com.slack.slack.common.entity.validator;

import com.slack.slack.common.code.ErrorCode;
import com.slack.slack.common.entity.*;
import com.slack.slack.common.exception.UnauthorizedException;
import com.slack.slack.common.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CardValidator extends PermissionValidator {

    public CardValidator(TeamMemberRepository teamMemberRepository) {
        super(teamMemberRepository);
    }

//    public void checkTeamOwnerOrBoardOwnerOrCardOwner(Card card, User user) {
//
//        Board board = card.getBoard();
//
//        Team team = board.getTeam();
//
//        TeamMember teamMember = super.teamMemberRepository.findByTeamAndUser(team, user)
//                .orElseThrow(() -> new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE));
//
//        boolean isTeamOwner = team.getUser().equals(user);
//
//        boolean isBoardOwner = board.getTeamMember().equals(teamMember);
//
//        boolean isCardOwner = card.getTeamMember().equals(teamMember);
//
//        if (!isTeamOwner && !isBoardOwner && !isCardOwner)
//            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE);
//    }

    public void checkCardOwner(Card card, User modifier) {
        if (!modifier.equals(card.getTeamMember().getUser()))
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE);
    }
}
