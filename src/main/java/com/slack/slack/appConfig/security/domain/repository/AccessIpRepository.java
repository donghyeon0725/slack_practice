package com.slack.slack.appConfig.security.domain.repository;

import com.slack.slack.appConfig.security.domain.entity.AccessIp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessIpRepository extends JpaRepository<AccessIp, Long> {
}
