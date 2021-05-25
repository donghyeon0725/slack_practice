package com.slack.slack.system;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Key {
    JOIN_KEY("join"),
    MESSAGE("message"),
    INVITE_KEY("invite");

    private String key;
}
