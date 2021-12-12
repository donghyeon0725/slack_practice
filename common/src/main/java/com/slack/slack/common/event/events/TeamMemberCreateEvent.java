package com.slack.slack.common.event.events;

import com.slack.slack.common.entity.Card;
import com.slack.slack.common.entity.TeamMember;

public class TeamMemberCreateEvent extends DomainEvent {
    public TeamMemberCreateEvent(TeamMember teamMember) {
        super(teamMember);
    }
}
