package com.slack.slack.common.messageBroker.kafka.common.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GroupID {
    ADMIN_LOGOUT_EVENT_GROUP("SLACK_ADMIN_LOGOUT_EVENT_GROUP"),

    // 카드 이벤트
    REST_CARD_ADD_EVENT_GROUP("REST_CARD_ADD_EVENT_GROUP"),
    REST_CARD_UPDATE_EVENT_GROUP("REST_CARD_UPDATE_EVENT_GROUP"),
    REST_CARD_DELETE_EVENT_GROUP("REST_CARD_DELETE_EVENT_GROUP"),
    REST_CARD_REFRESH_EVENT_GROUP("REST_CARD_REFRESH_EVENT_GROUP")


    ;



    private String groupID;
}
