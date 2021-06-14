package com.slack.slack.domain.card;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.slack.slack.domain.board.Board;
import com.slack.slack.domain.team.TeamActivity;
import com.slack.slack.domain.team.TeamMember;
import com.slack.slack.system.State;
import lombok.*;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@JsonFilter("Card")
@Builder
@Where(clause = "state != 'DELETED'")
public class Card {
    @Id
    @GeneratedValue
    private Integer id;

    @OneToMany(mappedBy = "card")
    private List<TeamActivity> teamActivities;

    @ManyToOne(fetch = FetchType.LAZY)
    private TeamMember teamMember;

    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;

    private String title;

    private String content;

    private Integer position;

    @Enumerated(EnumType.STRING)
    private State state;

    private Date date;

    @OneToMany(mappedBy = "card")
    private List<Reply> replies;

    @Where(clause = "state != 'DELETED'")
    @OneToMany(mappedBy = "card")
    private List<Attachment> attachments;

    @Transient
    private boolean isSelected = false;

}
