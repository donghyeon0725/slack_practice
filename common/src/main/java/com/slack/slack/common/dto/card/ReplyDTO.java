package com.slack.slack.common.dto.card;

import com.slack.slack.common.dto.team.TeamMemberDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class ReplyDTO {
    private Integer replyId;

    private String content;

    private Date date;

    private TeamMemberDTO teamMember;
}
