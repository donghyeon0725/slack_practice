package com.slack.slack.common.repository;

import com.slack.slack.common.entity.Team;
import com.slack.slack.common.entity.TeamActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamActivityRepository extends JpaRepository<TeamActivity, Integer> {
    List<TeamActivity> findByTeam(Team team);
}
