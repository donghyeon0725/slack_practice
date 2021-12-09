package com.slack.slack.common.event.events;

import com.slack.slack.common.entity.Card;
import com.slack.slack.common.entity.Team;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardAddEvent extends DomainEvent {
    private Team team;
    private Card card;

    public CardAddEvent(Team team, Card card) {
        super(card);
        this.team = team;
        this.card = card;
    }
}
