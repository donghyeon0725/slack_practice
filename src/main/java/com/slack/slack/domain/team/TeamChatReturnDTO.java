package com.slack.slack.domain.team;

import com.slack.slack.system.State;
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
