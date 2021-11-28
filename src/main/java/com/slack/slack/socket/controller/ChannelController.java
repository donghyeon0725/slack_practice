package com.slack.slack.socket.controller;

import com.slack.slack.common.util.ResponseHeaderManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
