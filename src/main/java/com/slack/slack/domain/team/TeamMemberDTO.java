package com.slack.slack.domain.team;

import com.slack.slack.domain.user.User;
import com.slack.slack.system.State;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(description = "팀 멤버 정보")
public class TeamMemberDTO {
    private Integer id;

    private Integer teamId;

    private Integer userId;

    private State state;
}
