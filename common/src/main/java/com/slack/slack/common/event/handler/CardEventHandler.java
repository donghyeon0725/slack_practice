package com.slack.slack.common.event.handler;

import com.slack.slack.common.code.Activity;
import com.slack.slack.common.dto.card.CardReturnDTO;
import com.slack.slack.common.entity.Card;
import com.slack.slack.common.entity.Team;
import com.slack.slack.common.entity.TeamActivity;
import com.slack.slack.common.event.events.*;
import com.slack.slack.common.repository.TeamActivityRepository;
import com.slack.slack.common.socket.SubscriptionHub;
import com.slack.slack.common.socket.handlers.channel.Channel;
import com.slack.slack.common.util.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CardEventHandler {
    private final ModelMapper modelMapper;

    private final TeamActivityRepository teamActivityRepository;

    // 카드 추가 이벤트
    @EventListener
    public void handle(CardAddEvent event) {
        Team team = event.getTeam();
        CardReturnDTO card = modelMapper.map(event.getCard(), CardReturnDTO.class);

        Map<String, Object> data = new HashMap<>();
        data.put("type", "onCardAdd");
        data.put("data", card);

        SubscriptionHub.send(
            Channel.TEAM.getChnnelAt(team.getId().toString()),
            JsonUtils.toJson(data)
        );
    }

    // 카드 생성시 활동 알람 생성
    @EventListener
    public void handle(CardCreateEvent event) {
        Card card = event.getCard();

        teamActivityRepository.save(
                TeamActivity.builder()
                        .card(card)
                        .teamMember(card.getTeamMember())
                        .date(new Date())
                        .board(card.getBoard())
                        .detail(Activity.CARD_CREATED)
                        .build()
        );
    }

    // 업데이트 이벤트
    @EventListener
    public void handle(CardUpdateEvent event) {
        Team team = event.getTeam();
        CardReturnDTO card = modelMapper.map(event.getCard(), CardReturnDTO.class);

        Map<String, Object> data = new HashMap<>();
        data.put("type", "onCardUpdate");
        data.put("data", card);

        SubscriptionHub.send(
            Channel.TEAM.getChnnelAt(team.getId().toString()),
            JsonUtils.toJson(data)
        );
    }

    // 삭제 이벤드
    @EventListener
    public void handle(CardDeleteEvent event) {
        Team team = event.getTeam();
        CardReturnDTO card = modelMapper.map(event.getCard(), CardReturnDTO.class);

        Map<String, Object> data = new HashMap<>();
        data.put("type", "onCardDelete");
        data.put("data", card);

        SubscriptionHub.send(
            Channel.TEAM.getChnnelAt(team.getId().toString()),
            JsonUtils.toJson(data)
        );
    }

    // 업데이트
    @EventListener
    public void handle(CardRefreshEvent event) {
        Team team = event.getTeam();

        Map<String, Object> data = new HashMap<>();
        data.put("type", "onRefreshCards");

        SubscriptionHub.send(
            Channel.TEAM.getChnnelAt(team.getId().toString()),
            JsonUtils.toJson(data)
        );
    }

}
