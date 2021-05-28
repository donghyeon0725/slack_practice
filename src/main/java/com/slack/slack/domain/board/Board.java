package com.slack.slack.domain.board;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.slack.slack.domain.team.Team;
import com.slack.slack.domain.team.TeamActivity;
import com.slack.slack.domain.team.TeamMember;
import com.slack.slack.system.State;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@JsonFilter("Board")
@Builder
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

    @Enumerated(EnumType.STRING)
    private State state;

    private Date date;

    @OneToMany(mappedBy = "board")
    private List<TeamActivity> teamActivities;
}
