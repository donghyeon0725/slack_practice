package com.slack.slack.common.entity.validator;

import com.slack.slack.common.code.ErrorCode;
import com.slack.slack.common.dto.team.TeamCommand;
import com.slack.slack.common.entity.Team;
import com.slack.slack.common.entity.User;
import com.slack.slack.common.exception.InvalidInputException;
import com.slack.slack.common.exception.ResourceConflict;
import com.slack.slack.common.exception.UnauthorizedException;
import com.slack.slack.common.repository.TeamMemberRepository;
import com.slack.slack.common.repository.TeamRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TeamValidator extends PermissionValidator {

    private final TeamRepository teamRepository;

    public TeamValidator(TeamMemberRepository teamMemberRepository, TeamRepository teamRepository) {
        super(teamMemberRepository);
        this.teamRepository = teamRepository;
    }

    public void checkHasNoTeam(User user) {
        List<Team> teams = teamRepository.findByUser(user);
        if (teams.size() > 0)
            throw new ResourceConflict(ErrorCode.RESOURCE_CONFLICT);
    }

    public void checkValidation(TeamCommand teamCommand) {
        if (teamCommand.getId() == null)
            throw new InvalidInputException(ErrorCode.INVALID_INPUT_VALUE);
    }

    public void checkTeamOwner(Team team, User user) {
        if (team.getUser() != user)
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE);
    }

    public void checkAlreadyIsTeamMember(Team team, User invitedUser) {
        // 이미 있을시 예외
        teamMemberRepository.findByTeamAndUser(team, invitedUser).ifPresent(teamMember -> {
            throw new ResourceConflict(ErrorCode.RESOURCE_CONFLICT);
        });
    }

    public void checkTeamMember(Team team, User user) {
        if (teamMemberRepository.countByTeamAndUser(team, user) < 1) {
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE);
        }
    }
}
