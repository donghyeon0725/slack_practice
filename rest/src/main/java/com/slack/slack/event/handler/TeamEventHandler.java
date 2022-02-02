package com.slack.slack.event.handler;

import com.slack.slack.common.code.Activity;
import com.slack.slack.common.entity.Card;
import com.slack.slack.common.entity.Team;
import com.slack.slack.common.entity.TeamActivity;
import com.slack.slack.common.entity.TeamMember;
import com.slack.slack.common.event.events.CardCreateEvent;
import com.slack.slack.common.event.events.TeamMemberCreateEvent;
import com.slack.slack.common.repository.TeamActivityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class TeamEventHandler {
    private final TeamActivityRepository teamActivityRepository;

    // 카드 생성시 활동 알람 생성
    @EventListener
    public void handle(TeamMemberCreateEvent event) {
        TeamMember teamMember = (TeamMember) event.getDomain();
        Team team = teamMember.getTeam();

        teamActivityRepository.save(
                TeamActivity.builder()
                        .team(team)
                        .teamMember(teamMember)
                        .detail(Activity.TEAM_CREATED)
                        .date(new Date())
                        .build()
        );
    }

}
