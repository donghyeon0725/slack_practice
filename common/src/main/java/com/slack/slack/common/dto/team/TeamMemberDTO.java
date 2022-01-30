package com.slack.slack.common.dto.team;

import com.slack.slack.common.dto.user.UserDTO;
import com.slack.slack.common.code.Status;
import com.slack.slack.common.entity.TeamMember;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class TeamMemberDTO {
    private Integer teamMemberId;

    private TeamDTO team;

    private UserDTO user;

    private Status status;

    private Date date;

    public TeamMemberDTO(TeamMember teamMember) {
        BeanUtils.copyProperties(teamMember, this);
    }
}
