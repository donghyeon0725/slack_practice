package com.slack.slack.domain.team;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.slack.slack.domain.board.Board;
import com.slack.slack.domain.user.User;
import com.slack.slack.system.Activity;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@AllArgsConstructor
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@JsonFilter("TeamActivity")
@Builder
public class TeamActivity {
    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    private TeamMember teamMember;

    private Integer cardId;

    private Integer replyId;

    private Integer attachmentId;

    @Enumerated(EnumType.STRING)
    private Activity detail;

    private Date date;
}
