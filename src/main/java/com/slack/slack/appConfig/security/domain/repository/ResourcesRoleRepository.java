package com.slack.slack.appConfig.security.domain.repository;

import com.slack.slack.appConfig.security.domain.entity.ResourcesRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourcesRoleRepository extends JpaRepository<ResourcesRole, Long> {
}
