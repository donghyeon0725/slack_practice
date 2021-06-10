package com.slack.slack.domain.team;

import com.slack.slack.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Integer> {
    Optional<List<TeamMember>> findByUser_Id(Integer userId);

    Optional<TeamMember> findByTeamAndUser(Team team, User user);

    Optional<List<TeamMember>> findByTeam(Team team);
}
