package com.slack.slack.event.handler;

import com.slack.slack.common.code.Activity;
import com.slack.slack.common.dto.card.CardDTO;
import com.slack.slack.common.entity.Card;
import com.slack.slack.common.entity.Team;
import com.slack.slack.common.entity.TeamActivity;
import com.slack.slack.common.event.events.*;
import com.slack.slack.common.messageBroker.Message;
import com.slack.slack.common.messageBroker.kafka.common.code.Topic;
import com.slack.slack.common.messageBroker.kafka.sender.KafkaMessageSender;
import com.slack.slack.common.repository.TeamActivityRepository;
import com.slack.slack.common.socket.channel.Channel;
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
    private final TeamActivityRepository teamActivityRepository;

    private final KafkaMessageSender messageSender;

    // 카드 추가 이벤트
    @EventListener
    public void handle(CardAddEvent event) {
        Team team = event.getTeam();
        CardDTO card = new CardDTO(event.getCard());

        Map<String, Object> data = new HashMap<>();
        data.put("type", "onCardAdd");
        data.put("data", card);


        messageSender.send(
                Message.of(
                        Topic.REST_CARD_ADD_EVENT,
                        Channel.TEAM.getKey(team.getTeamId().toString()),
                        JsonUtils.toJson(data)
                )
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
        CardDTO card = new CardDTO(event.getCard());

        Map<String, Object> data = new HashMap<>();
        data.put("type", "onCardUpdate");
        data.put("data", card);


        messageSender.send(
                Message.of(
                        Topic.REST_CARD_UPDATE_EVENT,
                        Channel.TEAM.getKey(team.getTeamId().toString()),
                        JsonUtils.toJson(data)
                )
        );
    }

    // 삭제 이벤드
    @EventListener
    public void handle(CardDeleteEvent event) {
        Team team = event.getTeam();
        CardDTO card = new CardDTO(event.getCard());

        Map<String, Object> data = new HashMap<>();
        data.put("type", "onCardDelete");
        data.put("data", card);


        messageSender.send(
                Message.of(
                        Topic.REST_CARD_DELETE_EVENT,
                        Channel.TEAM.getKey(team.getTeamId().toString()),
                        JsonUtils.toJson(data)
                )
        );
    }

    // 업데이트
    @EventListener
    public void handle(CardRefreshEvent event) {
        Team team = event.getTeam();

        Map<String, Object> data = new HashMap<>();
        data.put("type", "onRefreshCards");


        messageSender.send(
                Message.of(
                        Topic.REST_CARD_REFRESH_EVENT,
                        Channel.TEAM.getKey(team.getTeamId().toString()),
                        JsonUtils.toJson(data)
                )
        );
    }

}
