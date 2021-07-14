package com.slack.slack.domain.card;

import com.slack.slack.domain.team.TeamMemberReturnDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ReplyReturnDTO {
    private Integer id;

    private String content;

    private Date date;

    private TeamMemberReturnDTO teamMember;
}
