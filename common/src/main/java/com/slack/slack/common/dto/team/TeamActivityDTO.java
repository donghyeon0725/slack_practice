package com.slack.slack.common.dto.team;

import com.slack.slack.common.code.Activity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class TeamActivityDTO {
    private Integer teamActivityId;

    private Activity detail;

    private Date date;
}
