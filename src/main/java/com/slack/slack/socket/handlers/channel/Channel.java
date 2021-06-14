package com.slack.slack.socket.handlers.channel;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Channel {
    TEAM("/team/%s");

    private String channel;

    public String getChnnelAt(String value) {
        return String.format(this.channel, value);
    }

}
