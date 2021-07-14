package com.slack.slack.domain.team;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.slack.slack.domain.board.Board;
import com.slack.slack.domain.board.BoardReturnDTO;
import com.slack.slack.domain.card.Card;
import com.slack.slack.domain.card.CardReturnDTO;
import com.slack.slack.domain.card.Reply;
import com.slack.slack.domain.card.ReplyReturnDTO;
import com.slack.slack.domain.user.User;
import com.slack.slack.domain.user.UserReturnDTO;
import com.slack.slack.system.State;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@JsonFilter("TeamMember")
@Getter
@Setter
public class TeamMemberReturnDTO {
    private Integer id;

    private TeamReturnDTO team;

    private UserReturnDTO user;

    private State state;

    private Date date;
}
