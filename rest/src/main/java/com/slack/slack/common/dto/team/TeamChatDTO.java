package com.slack.slack.common.dto.team;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamChatDTO {
    private Integer id;

    private String email;

    private String description;

    private Integer teamId;

    private Integer userId;
}
