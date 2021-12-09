package com.slack.slack.common.event.events;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DomainEvent {
    private Object domain;

    protected DomainEvent(Object domain) {
        this.domain = domain;
    }

    public Object getDomain() {
        return this.domain;
    }
}
