package com.slack.slack.common.dto.card;

import com.slack.slack.common.dto.team.TeamMemberReturnDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ReplyReturnDTO {
    private Integer replyId;

    private String content;

    private Date date;

    private TeamMemberReturnDTO teamMember;
}
