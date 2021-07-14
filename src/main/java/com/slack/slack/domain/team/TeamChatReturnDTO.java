package com.slack.slack.domain.team;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.slack.slack.domain.user.User;
import com.slack.slack.domain.user.UserReturnDTO;
import com.slack.slack.system.State;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@JsonFilter("TeamChat")
public class TeamChatReturnDTO {

    private Integer id;

    private String email;

    private String description;

    private State state;

    private Date date;
}
