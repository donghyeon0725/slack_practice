package com.slack.slack.common.event.events;

import com.slack.slack.common.entity.Board;

public class BoardCreateEvent extends DomainEvent {
    public BoardCreateEvent(Board board) {
        super(board);
    }

    public Board getBoard() {
        return (Board) super.getDomain();
    }
}
