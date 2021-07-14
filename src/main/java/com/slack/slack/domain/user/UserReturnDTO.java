package com.slack.slack.domain.user;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.slack.slack.domain.team.TeamChatReturnDTO;
import com.slack.slack.domain.team.TeamMemberReturnDTO;
import com.slack.slack.domain.team.TeamReturnDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class UserReturnDTO {
    private Integer id;

    private String email;

    private String name;

    private String state;

    private Date date;
}
