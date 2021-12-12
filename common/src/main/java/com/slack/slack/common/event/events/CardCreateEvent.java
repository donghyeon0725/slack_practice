package com.slack.slack.common.event.events;

import com.slack.slack.common.entity.Card;
import com.slack.slack.common.entity.Team;

public class CardCreateEvent extends DomainEvent {
    public CardCreateEvent(Card card) {
        super(card);
    }

    public Card getCard() {
        return (Card) super.getDomain();
    }
}
