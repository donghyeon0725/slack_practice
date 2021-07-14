package com.slack.slack.listener.event.card;

import com.slack.slack.domain.card.Card;
import com.slack.slack.domain.team.Team;
import com.slack.slack.domain.team.TeamChat;
import com.slack.slack.listener.event.chat.TeamChatAddEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardAddEvent {
    private Team team;
    private Card card;

    public CardAddEvent(Team team, Card card) {
        this.team = team;
        this.card = card;
    }
}
