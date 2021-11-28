package com.slack.slack.common.repository;

import com.slack.slack.common.entity.Resources;
import com.slack.slack.common.entity.ResourcesRole;
import com.slack.slack.common.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourcesRoleRepository extends JpaRepository<ResourcesRole, Long> {
    ResourcesRole findByResourcesAndRole(Resources resources, Role role);

    void deleteByResources(Resources resources);
}
