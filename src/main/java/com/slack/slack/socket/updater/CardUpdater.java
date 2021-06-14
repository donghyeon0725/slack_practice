package com.slack.slack.socket.updater;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.slack.slack.domain.card.Card;
import com.slack.slack.domain.team.Team;
import com.slack.slack.requestmanager.ResponseFilterManager;
import com.slack.slack.requestmanager.ResponseHeaderManager;
import com.slack.slack.socket.SubscriptionHub;
import com.slack.slack.socket.handlers.channel.Channel;
import com.slack.slack.system.State;
import com.slack.slack.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class CardUpdater {
    private final SimpleBeanPropertyFilter boardFilter = SimpleBeanPropertyFilter.filterOutAllExcept("id", "date", "state", "title", "content");
    private final SimpleBeanPropertyFilter memberFilter = SimpleBeanPropertyFilter.filterOutAllExcept("user");
    private final SimpleBeanPropertyFilter userFilter = SimpleBeanPropertyFilter.filterOutAllExcept("id", "name", "email");
    private final SimpleBeanPropertyFilter teamFilter = SimpleBeanPropertyFilter.filterOutAllExcept();
    private final SimpleBeanPropertyFilter cardFilter = SimpleBeanPropertyFilter.filterOutAllExcept("id", "board", "teamMember", "title", "content", "position", "state", "date", "attachments", "replies");

    private final SimpleBeanPropertyFilter replyFilter = SimpleBeanPropertyFilter.filterOutAllExcept("content", "date", "id", "teamMember");
    private final SimpleBeanPropertyFilter attachmentFilter = SimpleBeanPropertyFilter.filterOutAllExcept("id", "path", "systemFilename", "filename", "description", "date", "state");
    private final SimpleBeanPropertyFilter activityFilter = SimpleBeanPropertyFilter.filterOutAllExcept();
    private final FilterProvider filters = new SimpleFilterProvider()
            .addFilter("Activity", activityFilter).addFilter("Attachment", attachmentFilter)
            .addFilter("Reply", replyFilter).addFilter("Board", boardFilter)
            .addFilter("Team", teamFilter).addFilter("TeamMember", memberFilter)
            .addFilter("Card", cardFilter).addFilter("User", userFilter);

    public void onCardAdded(Team team, Card card) {

        try {
            Map<String, Object> data = new HashMap<>();
            data.put("type", "onCardAdd");
            data.put("data", JsonUtils.objectMapperSettingFilter(filters).writeValueAsString(card));

            SubscriptionHub.send(
                    Channel.TEAM.getChnnelAt(team.getId().toString()),
                    JsonUtils.toJson(data)
            );
        } catch (JsonProcessingException e) {
            log.error("error when parse object to json in " + this.getClass());
        }
    }

    public void onCardUpdated(Team team, Card card) {

        try {
            Map<String, Object> data = new HashMap<>();
            data.put("type", "onCardUpdate");
            data.put("data", JsonUtils.objectMapperSettingFilter(filters).writeValueAsString(card));

            SubscriptionHub.send(
                    Channel.TEAM.getChnnelAt(team.getId().toString()),
                    JsonUtils.toJson(data)
            );
        } catch (JsonProcessingException e) {
            log.error("error when parse object to json in " + this.getClass());
        }
    }

    public void onCardDeleted(Team team, Card card) {

        try {
            Map<String, Object> data = new HashMap<>();
            data.put("type", "onCardDelete");
            data.put("data", JsonUtils.objectMapperSettingFilter(filters).writeValueAsString(card));

            SubscriptionHub.send(
                    Channel.TEAM.getChnnelAt(team.getId().toString()),
                    JsonUtils.toJson(data)
            );
        } catch (JsonProcessingException e) {
            log.error("error when parse object to json in " + this.getClass());
        }
    }

    public void onCardRefreshed(Team team) {

        Map<String, Object> data = new HashMap<>();
        data.put("type", "onRefreshCards");

        SubscriptionHub.send(
                Channel.TEAM.getChnnelAt(team.getId().toString()),
                JsonUtils.toJson(data)
        );
    }


}
