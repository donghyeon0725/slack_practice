package com.slack.slack.appConfig.security.domain.repository;

import com.slack.slack.appConfig.security.domain.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByRoleName(String name);
}
