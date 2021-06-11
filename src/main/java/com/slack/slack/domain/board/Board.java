package com.slack.slack.domain.board;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.slack.slack.domain.card.Card;
import com.slack.slack.domain.team.Team;
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
@JsonFilter("Board")
@Builder
@Where(clause = "state != 'DELETED'")
public class Board {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    private TeamMember teamMember;

    @ManyToOne(fetch = FetchType.LAZY)
    private Team team;

    private String title;

    private String content;

    private String bannerPath;

    @Enumerated(EnumType.STRING)
    private State state;

    private Date date;

    @OneToMany(mappedBy = "board")
    private List<TeamActivity> teamActivities;

    @Where(clause = "state != 'DELETED'")
    @OneToMany(mappedBy = "board")
    private List<Card> cards;

    public boolean isBannerEmpty() {
        return this.bannerPath == null || this.bannerPath.replaceAll(" ", "").equals("");
    }
}
