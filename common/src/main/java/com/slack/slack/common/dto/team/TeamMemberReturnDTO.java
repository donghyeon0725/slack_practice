package com.slack.slack.common.dto.team;

import com.slack.slack.common.dto.user.UserReturnDTO;
import com.slack.slack.common.code.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TeamMemberReturnDTO {
    private Integer id;

    private TeamReturnDTO team;

    private UserReturnDTO user;

    private Status status;

    private Date date;
}
