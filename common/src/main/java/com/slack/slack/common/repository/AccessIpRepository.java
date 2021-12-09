package com.slack.slack.common.repository;

import com.slack.slack.common.entity.AccessIp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessIpRepository extends JpaRepository<AccessIp, Long> {
}
