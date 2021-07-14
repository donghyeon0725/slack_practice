package com.slack.slack.listener.event.chat;

import com.slack.slack.domain.team.TeamChat;

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
