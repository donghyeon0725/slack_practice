package com.slack.slack.socket.controller;

import com.slack.slack.appConfig.security.TokenManager;
import com.slack.slack.domain.card.Card;
import com.slack.slack.domain.card.CardDTO;
import com.slack.slack.requestmanager.ResponseFilterManager;
import com.slack.slack.requestmanager.ResponseHeaderManager;
import com.slack.slack.socket.model.UserId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChannelController {
    private String realTimeServerUrl = "/rt";

    @GetMapping("/socket")
    public ResponseEntity getMyData() {
        return new ResponseEntity(
                this.realTimeServerUrl
                , ResponseHeaderManager.headerWithThisPath()
                , HttpStatus.OK
        );
    }
}
