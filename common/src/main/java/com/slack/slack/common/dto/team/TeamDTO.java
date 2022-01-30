package com.slack.slack.common.dto.team;

import com.slack.slack.common.dto.user.UserDTO;
import com.slack.slack.common.code.Status;
import com.slack.slack.common.entity.Team;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class TeamDTO {
    private Integer teamId;

    private UserDTO user;

    private String name;

    private String description;

    private Status status;

    private Date date;

    public TeamDTO(Team team) {
        BeanUtils.copyProperties(team, this);
    }
}
