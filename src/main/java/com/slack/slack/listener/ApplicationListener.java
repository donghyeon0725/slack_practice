package com.slack.slack.listener;

import com.slack.slack.appConfig.security.domain.service.SecurityResourceService;
import com.slack.slack.appConfig.security.jwt.metadata.UrlFilterInvocationSecurityMetadataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 데이터 세팅 등등을 위한 클래스 입니다
 * 각각 애플리케이션 시점에 따라서 호출 되는 메소드의 모임 입니다.
 * */
@Component
@RequiredArgsConstructor
public class ApplicationListener {
    
    private final UrlFilterInvocationSecurityMetadataSource resourceMetadataSource;
    
    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() {
        resourceMetadataSource.reload();
    }
}
