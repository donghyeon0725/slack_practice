package com.slack.slack.domain.team;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.slack.slack.domain.board.Board;
import com.slack.slack.domain.card.Card;
import com.slack.slack.domain.card.Reply;
import com.slack.slack.domain.user.User;
import com.slack.slack.system.State;
import lombok.*;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@JsonFilter("TeamMember")
@Builder
public class TeamMember {
    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToMany(mappedBy = "teamMember")
    private List<Board> boards;

    @OneToMany(mappedBy = "teamMember")
    private List<TeamActivity> teamActivities;

    @NonNull
    @Enumerated(EnumType.STRING)
    private State state;

    private Date date;

    @OneToMany(mappedBy = "teamMember")
    private List<Card> cards;

    @OneToMany(mappedBy = "teamMember")
    private List<Reply> replies;

}
