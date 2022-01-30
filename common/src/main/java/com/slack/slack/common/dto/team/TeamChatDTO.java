package com.slack.slack.common.dto.team;

import com.slack.slack.common.code.Status;
import com.slack.slack.common.entity.TeamChat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class TeamChatDTO {

    private Integer teamChatId;

    private String email;

    private String description;

    private Status status;

    private Date date;

    public TeamChatDTO(TeamChat teamChat) {
        BeanUtils.copyProperties(teamChat, this);
    }
}
