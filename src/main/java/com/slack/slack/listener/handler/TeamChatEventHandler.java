package com.slack.slack.listener.handler;

import com.slack.slack.common.entity.TeamChat;
import com.slack.slack.common.dto.team.TeamChatReturnDTO;
import com.slack.slack.listener.event.chat.TeamChatAddEvent;
import com.slack.slack.listener.event.chat.TeamChatUpdateEvent;
import com.slack.slack.socket.SubscriptionHub;
import com.slack.slack.socket.handlers.channel.Channel;
import com.slack.slack.common.util.JsonUtils;
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
public class TeamChatEventHandler {

    private final ModelMapper modelMapper;

    // 채팅 추가 이벤트
    @EventListener
    public void handle(TeamChatAddEvent event) {
        TeamChat chat = event.getChat();
        TeamChatReturnDTO teamChatReturnDTO = modelMapper.map(chat, TeamChatReturnDTO.class);

        Map<String, Object> data = new HashMap<>();
        data.put("type", "onChatAdded");
        data.put("data", teamChatReturnDTO);

        SubscriptionHub.send(
            Channel.TEAM.getChnnelAt(chat.getTeam().getId().toString()),
            JsonUtils.toJson(data)
        );
    }

    // 채팅 업데이트 이벤트
    @EventListener
    public void handle(TeamChatUpdateEvent event) {
        TeamChat chat = event.getChat();
        TeamChatReturnDTO teamChatReturnDTO = modelMapper.map(chat, TeamChatReturnDTO.class);

        Map<String, Object> data = new HashMap<>();
        data.put("type", "onChatUpdated");
        data.put("data", teamChatReturnDTO);

        SubscriptionHub.send(
            Channel.TEAM.getChnnelAt(chat.getTeam().getId().toString()),
            JsonUtils.toJson(data)
        );
    }
}
