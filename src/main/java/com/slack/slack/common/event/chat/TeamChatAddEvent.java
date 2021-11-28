package com.slack.slack.common.event.chat;

import com.slack.slack.common.entity.TeamChat;

public class TeamChatAddEvent {
    private TeamChat chat;

    public TeamChatAddEvent(TeamChat chat) {
        this.chat = chat;
    }

    public TeamChat getChat() {
        return chat;
    }

    public TeamChatAddEvent setChat(TeamChat chat) {
        this.chat = chat;
        return this;
    }
}
