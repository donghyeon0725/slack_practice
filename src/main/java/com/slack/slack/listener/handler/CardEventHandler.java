package com.slack.slack.listener.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.slack.slack.domain.card.Card;
import com.slack.slack.domain.card.CardReturnDTO;
import com.slack.slack.domain.team.Team;
import com.slack.slack.domain.team.TeamChat;
import com.slack.slack.domain.team.TeamChatReturnDTO;
import com.slack.slack.listener.event.card.CardAddEvent;
import com.slack.slack.listener.event.card.CardDeleteEvent;
import com.slack.slack.listener.event.card.CardRefreshEvent;
import com.slack.slack.listener.event.card.CardUpdateEvent;
import com.slack.slack.listener.event.chat.TeamChatAddEvent;
import com.slack.slack.socket.SubscriptionHub;
import com.slack.slack.socket.handlers.channel.Channel;
import com.slack.slack.util.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CardEventHandler {
    private final ModelMapper modelMapper;

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
