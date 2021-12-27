package com.slack.slack.common.entity.validator;

import com.slack.slack.common.code.ErrorCode;
import com.slack.slack.common.entity.*;
import com.slack.slack.common.exception.UnauthorizedException;
import com.slack.slack.common.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PermissionValidator {

    protected final TeamMemberRepository teamMemberRepository;

    public void checkTeamOwnerOrBoardOwnerOrCardOwner(Team team, Board board, Card card, User user) {

        TeamMember teamMember = teamMemberRepository.findByTeamAndUser(team, user)
                .orElseThrow(() -> new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE));

        boolean isTeamOwner = team.getUser().equals(user);

        boolean isBoardOwner = board.getTeamMember().equals(teamMember);

        boolean isCardOwner = card.getTeamMember().equals(teamMember);

        if (!isTeamOwner && !isBoardOwner && !isCardOwner)
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE);
    }

    public void checkTeamOwnerOrBoardOwner(Team team, Board board, User user) {

        TeamMember teamMember = teamMemberRepository.findByTeamAndUser(team, user)
                .orElseThrow(() -> new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE));

        boolean isBoardOwner = board.getTeamMember().getTeamMemberId().equals(teamMember.getTeamMemberId());

        boolean isTeamOwner = team.getUser().getUserId().equals(user.getUserId());

        if (!isBoardOwner && !isTeamOwner)
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE);
    }
}
