package com.slack.slack.domain.team;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.slack.slack.domain.board.Board;
import com.slack.slack.domain.board.BoardReturnDTO;
import com.slack.slack.domain.card.*;
import com.slack.slack.system.Activity;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@JsonFilter("TeamActivity")
public class TeamActivityReturnDTO {
    private Integer id;

    private Activity detail;

    private Date date;
}
