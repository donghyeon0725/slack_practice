package com.slack.slack.domain.team;

import com.slack.slack.system.Activity;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TeamActivityReturnDTO {
    private Integer id;

    private Activity detail;

    private Date date;
}
