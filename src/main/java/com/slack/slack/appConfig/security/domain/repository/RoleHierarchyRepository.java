package com.slack.slack.appConfig.security.domain.repository;

import com.slack.slack.appConfig.security.domain.entity.ResourcesRole;
import com.slack.slack.appConfig.security.domain.entity.RoleHierarchy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleHierarchyRepository extends JpaRepository<RoleHierarchy, Long> {
}
