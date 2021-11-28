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

    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    private TeamMember teamMember;

    @ManyToOne(fetch = FetchType.LAZY)
    private Card card;

    @ManyToOne(fetch = FetchType.LAZY)
    private Reply reply;

    @ManyToOne(fetch = FetchType.LAZY)
    private Attachment attachment;

    @Enumerated(EnumType.STRING)
    private Activity detail;

    private Date date;

    private BaseCreateEntity baseCreateEntity;
    private BaseModifyEntity baseModifyEntity;
}
