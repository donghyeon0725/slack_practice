package com.slack.slack.domain.team;

import com.slack.slack.domain.user.User;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Integer> {
    Optional<List<Team>> findByUserId(Integer userId);

    /* In 절을 사용할 수 있도록 합니다. team id로 팀을 검색합니다. */
    //Optional<List<Team>> findByIdIn(List<Integer> teamId);


    Optional<List<Team>> findByTeamMemberIn(List<TeamMember> teamMember);
}
