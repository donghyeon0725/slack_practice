package com.slack.slack.appConfig.security.form.repository;

import com.slack.slack.appConfig.security.form.domain.ResourcesRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourcesRoleRepository extends JpaRepository<ResourcesRole, Long> {
}
