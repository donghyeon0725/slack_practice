package com.slack.slack.listener.event.chat;

import com.slack.slack.domain.team.TeamChat;

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
