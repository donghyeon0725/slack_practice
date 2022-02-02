package com.slack.slack.common.event.events;

import com.slack.slack.common.entity.TeamChat;
import com.slack.slack.common.event.events.DomainEvent;

public class TeamChatUpdateEvent extends DomainEvent {
    public TeamChatUpdateEvent(TeamChat chat) {
        super(chat);
    }

    public TeamChat getTeamChat() {
        return (TeamChat) super.getDomain();
    }
}
