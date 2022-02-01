package com.slack.slack.security.hierarchy;

import com.slack.slack.common.service.RoleHierarchyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;

@RequiredArgsConstructor
public class SecurityRoleHierarchy extends RoleHierarchyImpl {
    private final RoleHierarchyService roleHierarchyService;

    public void reload() {
        super.setHierarchy(roleHierarchyService.findAllHierarchy());
    }

}
