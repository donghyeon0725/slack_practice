package com.slack.slack.domain.team;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamActivityRepository extends JpaRepository<TeamActivity, Integer> {
}
