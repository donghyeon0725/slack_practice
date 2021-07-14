package com.slack.slack.listener.event.card;

import com.slack.slack.domain.card.Card;
import com.slack.slack.domain.team.Team;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardUpdateEvent {
    private Team team;
    private Card card;

    public CardUpdateEvent(Team team, Card card) {
        this.team = team;
        this.card = card;
    }
}
