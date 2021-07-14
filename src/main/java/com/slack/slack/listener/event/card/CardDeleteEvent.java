package com.slack.slack.listener.event.card;

import com.slack.slack.domain.card.Card;
import com.slack.slack.domain.team.Team;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardDeleteEvent {
    private Team team;
    private Card card;

    public CardDeleteEvent(Team team, Card card) {
        this.team = team;
        this.card = card;
    }
}
