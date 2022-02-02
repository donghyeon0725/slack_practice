package com.slack.socket.socket.handlers.channel;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Channel {
    TEAM("/team/%s");

    private String channel;

    public String getChnnelAt(String value) {
        return String.format(this.channel, value);
    }

}
