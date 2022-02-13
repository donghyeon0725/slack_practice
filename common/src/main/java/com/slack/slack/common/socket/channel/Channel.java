package com.slack.slack.common.socket.channel;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Channel {
    TEAM("/team/%s");

    private String channel;

    public String getKey(String value) {
        return String.format(this.channel, value);
    }

}
