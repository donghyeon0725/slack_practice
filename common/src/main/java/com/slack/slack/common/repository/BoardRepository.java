package com.slack.slack.common.repository;

import com.slack.slack.common.entity.Board;
import com.slack.slack.common.entity.Team;
import com.slack.slack.common.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Integer> {
    List<Board> findByTeam(Team team);

    List<Board> findByTeamMember(TeamMember teamMember);
}
