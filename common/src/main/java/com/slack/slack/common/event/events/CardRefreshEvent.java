package com.slack.slack.common.event.events;

import com.slack.slack.common.entity.Team;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardRefreshEvent extends DomainEvent {
    private Team team;

    public CardRefreshEvent(Team team) {
        super(team);
        this.team = team;
    }
}
