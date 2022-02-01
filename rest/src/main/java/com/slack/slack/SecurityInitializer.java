package com.slack.slack;

import com.slack.slack.common.service.RoleHierarchyService;
import com.slack.slack.security.jwt.metadata.UrlFilterInvocationSecurityMetadataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.stereotype.Component;

/**
 * 권한 계층 적용을 위해 시스템이 로드 될 때, 동작합니다.
 * */
@Component
@RequiredArgsConstructor
public class SecurityInitializer implements ApplicationRunner {
    private final RoleHierarchyService roleHierarchyService;

    private final RoleHierarchyImpl roleHierarchy;

    private final UrlFilterInvocationSecurityMetadataSource resourceMetadataSource;


    @Override
    public void run(ApplicationArguments args) throws Exception {
        String allHierarchy = roleHierarchyService.findAllHierarchy();
        roleHierarchy.setHierarchy(allHierarchy);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() {
        resourceMetadataSource.reload();
    }
}
