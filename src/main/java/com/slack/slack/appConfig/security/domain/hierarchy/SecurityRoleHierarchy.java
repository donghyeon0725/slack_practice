package com.slack.slack.appConfig.security.domain.hierarchy;

import com.slack.slack.appConfig.security.domain.service.RoleHierarchyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;

@RequiredArgsConstructor
public class SecurityRoleHierarchy extends RoleHierarchyImpl {
    private final RoleHierarchyService roleHierarchyService;

    public void reload() {
        super.setHierarchy(roleHierarchyService.findAllHierarchy());
    }

}
