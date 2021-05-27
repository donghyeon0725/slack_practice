package com.slack.slack.domain.team;

import com.slack.slack.domain.user.User;
import com.slack.slack.system.State;
import lombok.Data;

@Data
public class TeamMemberDTO {
    private Integer id;

    private Integer teamId;

    private Integer userId;

    private State state;
}
