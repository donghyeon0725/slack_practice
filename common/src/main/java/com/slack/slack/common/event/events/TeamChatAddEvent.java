package com.slack.slack.common.event.events;

import com.slack.slack.common.entity.TeamChat;
import com.slack.slack.common.event.events.DomainEvent;

public class TeamChatAddEvent extends DomainEvent {

    public TeamChatAddEvent(TeamChat chat) {
        super(chat);
    }

    public TeamChat getTeamChat() {
        return (TeamChat) super.getDomain();
    }

}
