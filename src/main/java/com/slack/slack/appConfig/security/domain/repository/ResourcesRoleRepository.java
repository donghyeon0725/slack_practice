package com.slack.slack.appConfig.security.domain.repository;

import com.slack.slack.appConfig.security.domain.entity.Resources;
import com.slack.slack.appConfig.security.domain.entity.ResourcesRole;
import com.slack.slack.appConfig.security.domain.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResourcesRoleRepository extends JpaRepository<ResourcesRole, Long> {
    ResourcesRole findByResourcesAndRole(Resources resources, Role role);

    void deleteByResources(Resources resources);
}
