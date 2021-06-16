package com.slack.slack.domain.team;

import lombok.Data;

import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.SqlResultSetMapping;

@Data
public class TeamChatDTO {
    private Integer id;

    private String email;

    private String description;

    private Integer teamId;

    private Integer userId;
}
