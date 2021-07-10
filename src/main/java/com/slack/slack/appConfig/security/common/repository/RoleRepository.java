package com.slack.slack.appConfig.security.common.repository;

import com.slack.slack.appConfig.security.common.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByRoleName(String name);
}
