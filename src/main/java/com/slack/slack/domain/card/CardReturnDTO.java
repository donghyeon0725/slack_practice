package com.slack.slack.domain.card;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.slack.slack.domain.board.Board;
import com.slack.slack.domain.board.BoardReturnDTO;
import com.slack.slack.domain.team.TeamActivity;
import com.slack.slack.domain.team.TeamActivityReturnDTO;
import com.slack.slack.domain.team.TeamMember;
import com.slack.slack.domain.team.TeamMemberReturnDTO;
import com.slack.slack.system.State;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@JsonFilter("Card")
public class CardReturnDTO {

    private Integer id;

    private String title;

    private String content;

    private Integer position;

    private State state;

    private Date date;

    private List<AttachmentReturnDTO> attachments;

    private boolean isSelected = false;
}
