package com.slack.slack.domain.team;

import com.slack.slack.domain.user.UserReturnDTO;
import com.slack.slack.system.State;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TeamMemberReturnDTO {
    private Integer id;

    private TeamReturnDTO team;

    private UserReturnDTO user;

    private State state;

    private Date date;
}
