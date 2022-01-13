package com.slack.slack.common.dto.team;

import com.slack.slack.common.code.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TeamChatReturnDTO {

    private Integer teamChatId;

    private String email;

    private String description;

    private Status status;

    private Date date;
}
