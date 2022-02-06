package com.slack.slack.common.messageBroker.kafka.common.code;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Topic {
    ADMIN_LOGOUT_EVENT("SLACK_ADMIN_LOGOUT_EVENT", GroupID.ADMIN_LOGOUT_EVENT);

    private String topicName;

    @Getter(value = AccessLevel.PRIVATE)
    private GroupID groupID;

    public String getGroupID() {
        return groupID.getGroupID();
    }
}
