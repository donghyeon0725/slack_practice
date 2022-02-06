package com.slack.slack.common.messageBroker.kafka.common.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GroupID {
    ADMIN_LOGOUT_EVENT("SLACK_ADMIN_LOGOUT_EVENT_GROUP");

    private String groupID;
}
