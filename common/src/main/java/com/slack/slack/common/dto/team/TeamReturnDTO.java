package com.slack.slack.common.dto.team;

import com.slack.slack.common.dto.user.UserReturnDTO;
import com.slack.slack.common.code.State;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TeamReturnDTO {
    private Integer id;

    private UserReturnDTO user;

    private String name;

    private String description;

    private State state;

    private Date date;
}
