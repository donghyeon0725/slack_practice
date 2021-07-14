package com.slack.slack.domain.card;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.slack.slack.domain.team.TeamActivity;
import com.slack.slack.domain.team.TeamActivityReturnDTO;
import com.slack.slack.domain.team.TeamMember;
import com.slack.slack.domain.team.TeamMemberReturnDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@JsonFilter("Reply")
public class ReplyReturnDTO {
    private Integer id;

    private String content;

    private Date date;
}
