package com.slack.slack.common.repository;

import com.slack.slack.common.entity.Team;
import com.slack.slack.common.entity.TeamMember;
import com.slack.slack.common.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Integer> {
    List<Team> findByUser(User user);

    /* In 절을 사용할 수 있도록 합니다. team id로 팀을 검색합니다. */
    //Optional<List<Team>> findByIdIn(List<Integer> teamId);


    List<Team> findByTeamMemberIn(List<TeamMember> teamMember);
}
