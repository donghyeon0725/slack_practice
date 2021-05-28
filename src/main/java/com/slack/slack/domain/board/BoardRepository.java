package com.slack.slack.domain.board;

import com.slack.slack.domain.team.Team;
import com.slack.slack.domain.team.TeamMember;
import com.slack.slack.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Integer> {
    Optional<List<Board>> findByTeam(Team team);

    Optional<List<Board>> findByTeamMember(TeamMember teamMember);
}
