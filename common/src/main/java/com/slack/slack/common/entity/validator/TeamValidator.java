package com.slack.slack.common.entity.validator;

import com.slack.slack.common.code.ErrorCode;
import com.slack.slack.common.dto.team.TeamDTO;
import com.slack.slack.common.entity.Team;
import com.slack.slack.common.entity.User;
import com.slack.slack.common.exception.InvalidInputException;
import com.slack.slack.common.exception.ResourceConflict;
import com.slack.slack.common.exception.UnauthorizedException;
import com.slack.slack.common.repository.TeamMemberRepository;
import com.slack.slack.common.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TeamValidator {
    private final TeamMemberRepository teamMemberRepository;

    private final TeamRepository teamRepository;

    public void duplicationCheck(User user) {
        List<Team> teams = teamRepository.findByUser(user).get();
        if (teams.size() > 0)
            throw new ResourceConflict(ErrorCode.RESOURCE_CONFLICT);
    }

    public void validateTeamDTO(TeamDTO teamDTO) {
        if (teamDTO.getId() == null)
            throw new InvalidInputException(ErrorCode.INVALID_INPUT_VALUE);
    }

    public void checkAuthorization(Team team, User user) {
        if (team.getUser() != user)
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE);
    }

    public void validateTeamAndUserForInvite(Team team, User invitedUser) {
        // 이미 있을시 예외
        teamMemberRepository.findByTeamAndUser(team, invitedUser).ifPresent(teamMember -> {
            throw new ResourceConflict(ErrorCode.RESOURCE_CONFLICT);
        });
    }
}