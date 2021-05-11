package com.slack.slack.domain.message;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private MessageSource messageSource;

    public MessageController(MessageSource  messageSource) {
        this.messageSource = messageSource;
    }

    @GetMapping(path = "/getMessage")
    public String messageSource(@RequestHeader(name="Accept-Language", required = false) Locale locale) {
        return messageSource.getMessage("greeting.message", null, locale);
    }
}
