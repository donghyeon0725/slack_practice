package com.slack.slack.common.dto.team;

import com.slack.slack.common.code.Activity;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TeamActivityReturnDTO {
    private Integer teamActivityId;

    private Activity detail;

    private Date date;
}
