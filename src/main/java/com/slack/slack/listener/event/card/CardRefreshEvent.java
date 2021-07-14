package com.slack.slack.listener.event.card;

import com.slack.slack.domain.card.Card;
import com.slack.slack.domain.team.Team;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardRefreshEvent {
    private Team team;

    public CardRefreshEvent(Team team) {
        this.team = team;
    }
}
