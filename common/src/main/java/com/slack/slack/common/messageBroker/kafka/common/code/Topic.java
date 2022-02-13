package com.slack.slack.common.messageBroker.kafka.common.code;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Topic {
    ADMIN_LOGOUT_EVENT("SLACK_ADMIN_LOGOUT_EVENT", GroupID.ADMIN_LOGOUT_EVENT_GROUP),

    // 카드 이벤트
    REST_CARD_ADD_EVENT("REST_CARD_ADD_EVENT", GroupID.REST_CARD_ADD_EVENT_GROUP),
    REST_CARD_UPDATE_EVENT("REST_CARD_UPDATE_EVENT", GroupID.REST_CARD_UPDATE_EVENT_GROUP),
    REST_CARD_DELETE_EVENT("REST_CARD_DELETE_EVENT", GroupID.REST_CARD_DELETE_EVENT_GROUP),
    REST_CARD_REFRESH_EVENT("REST_CARD_REFRESH_EVENT", GroupID.REST_CARD_REFRESH_EVENT_GROUP)


    ;



    private String topicName;

    @Getter(value = AccessLevel.PRIVATE)
    private GroupID groupID;

    public String getGroupID() {
        return groupID.getGroupID();
    }
}
