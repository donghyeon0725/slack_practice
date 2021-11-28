package com.slack.slack.common.dto.team;

import com.slack.slack.common.code.State;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TeamChatReturnDTO {

    private Integer id;

    private String email;

    private String description;

    private State state;

    private Date date;
}
