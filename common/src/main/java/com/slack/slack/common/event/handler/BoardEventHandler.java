package com.slack.slack.common.event.handler;

import com.slack.slack.common.code.Activity;
import com.slack.slack.common.entity.Board;
import com.slack.slack.common.entity.TeamActivity;
import com.slack.slack.common.entity.TeamMember;
import com.slack.slack.common.event.events.BoardCreateEvent;
import com.slack.slack.common.event.events.CardDeleteEvent;
import com.slack.slack.common.repository.TeamActivityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class BoardEventHandler {

    private final TeamActivityRepository teamActivityRepository;

    @EventListener
    public void handle(BoardCreateEvent event) {

        Board board = event.getBoard();
        TeamMember member = board.getTeamMember();

        teamActivityRepository.save(
                TeamActivity.builder()
                        .board(board)
                        .teamMember(member)
                        .detail(Activity.BOARD_CREATED)
                        .date(new Date())
                        .build()
        );
    }
}
