package com.slack.slack.domain.team;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.slack.slack.domain.board.BoardReturnDTO;
import com.slack.slack.domain.user.UserReturnDTO;
import com.slack.slack.system.State;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@JsonFilter("Team")
public class TeamReturnDTO {
    private Integer id;

    private UserReturnDTO user;

    private String name;

    private String description;

    private State state;

    private Date date;
}
