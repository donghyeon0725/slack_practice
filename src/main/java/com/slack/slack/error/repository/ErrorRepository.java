package com.slack.slack.error.repository;

import com.slack.slack.error.ErrorResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ErrorRepository extends JpaRepository<ErrorResponse, Integer> {
}
