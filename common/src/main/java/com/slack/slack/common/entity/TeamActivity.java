package com.slack.slack.common.entity;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.slack.slack.common.code.Activity;
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

    @JoinColumn(name = "team_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Team team;

    @JoinColumn(name = "board_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;

    @JoinColumn(name = "team_member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private TeamMember teamMember;

    @JoinColumn(name = "card_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Card card;

    @JoinColumn(name = "reply_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Reply reply;

    @JoinColumn(name = "attachment_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Attachment attachment;

    @Enumerated(EnumType.STRING)
    private Activity detail;

    private Date date;

    private BaseCreateEntity baseCreateEntity;
    private BaseModifyEntity baseModifyEntity;
}
