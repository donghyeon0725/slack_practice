package com.slack.slack.common.socket.event.events;

import com.slack.slack.common.entity.TeamChat;

public class TeamChatUpdateEvent {
    private TeamChat chat;

    public TeamChatUpdateEvent(TeamChat chat) {
        this.chat = chat;
    }

    public TeamChat getChat() {
        return chat;
    }

    public TeamChatUpdateEvent setChat(TeamChat chat) {
        this.chat = chat;
        return this;
    }
}
