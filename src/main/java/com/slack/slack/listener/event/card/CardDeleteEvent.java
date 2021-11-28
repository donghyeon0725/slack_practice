package com.slack.slack.listener.event.card;

import com.slack.slack.common.entity.Card;
import com.slack.slack.common.entity.Team;
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
