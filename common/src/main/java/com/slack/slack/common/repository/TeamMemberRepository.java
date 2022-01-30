package com.slack.slack.common.repository;

import com.slack.slack.common.entity.Team;
import com.slack.slack.common.entity.TeamMember;
import com.slack.slack.common.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Integer> {
    List<TeamMember> findByUser(User user);

    Optional<TeamMember> findByTeamAndUser(Team team, User user);

    int countByTeamAndUser(Team team, User user);

    List<TeamMember> findByTeam(Team team);
}
