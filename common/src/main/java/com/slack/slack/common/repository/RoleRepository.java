package com.slack.slack.common.repository;

import com.slack.slack.common.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByRoleName(String name);
}
