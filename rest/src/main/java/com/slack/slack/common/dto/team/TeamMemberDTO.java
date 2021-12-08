package com.slack.slack.common.dto.team;

import com.slack.slack.common.code.State;
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
