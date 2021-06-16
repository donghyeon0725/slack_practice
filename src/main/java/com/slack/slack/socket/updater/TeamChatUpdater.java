package com.slack.slack.socket.updater;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.slack.slack.domain.card.Card;
import com.slack.slack.domain.team.Team;
import com.slack.slack.domain.team.TeamChat;
import com.slack.slack.socket.SubscriptionHub;
import com.slack.slack.socket.handlers.channel.Channel;
import com.slack.slack.system.State;
import com.slack.slack.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class TeamChatUpdater {

    private final SimpleBeanPropertyFilter teamChatFilter = SimpleBeanPropertyFilter.filterOutAllExcept("id", "email", "description", "date", "state");
    private final FilterProvider filters = new SimpleFilterProvider()
            .addFilter("TeamChat", teamChatFilter);

    public void onChatAdded(TeamChat chat) {
        try{
            Map<String, Object> data = new HashMap<>();
            data.put("type", "onChatAdded");
            data.put("data", JsonUtils.objectMapperSettingFilter(filters).writeValueAsString(chat));

            SubscriptionHub.send(
                    Channel.TEAM.getChnnelAt(chat.getTeam().getId().toString()),
                    JsonUtils.toJson(data)
            );
        } catch (JsonProcessingException e) {
            log.error("error when parse object to json in " + this.getClass());
        }
    }

    public void onChatUpdated(TeamChat chat) {

        try{
            Map<String, Object> data = new HashMap<>();
            data.put("type", "onChatUpdated");
            data.put("data", JsonUtils.objectMapperSettingFilter(filters).writeValueAsString(chat));

            SubscriptionHub.send(
                    Channel.TEAM.getChnnelAt(chat.getTeam().getId().toString()),
                    JsonUtils.toJson(data)
            );
        } catch (JsonProcessingException e) {
            log.error("error when parse object to json in " + this.getClass());
        }
    }
}
