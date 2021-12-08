package com.slack.slack.common.repository;

import com.slack.slack.common.response.ErrorResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ErrorRepository extends JpaRepository<ErrorResponse, Integer> {
}
