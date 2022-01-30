package com.slack.slack.common.dto.team;

import com.slack.slack.common.code.Status;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(description = "팀 멤버 정보")
public class TeamMemberCommand {
    private Integer id;

    private Integer teamId;

    private Integer userId;

    private Status status;
}
