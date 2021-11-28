package com.slack.slack.common.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Key {
    JOIN_KEY("join"),
    MESSAGE("message"),
    INVITE_KEY("invite"),
    SOCKET("websocket"),
    ROLES("roles");

    private String key;

}
