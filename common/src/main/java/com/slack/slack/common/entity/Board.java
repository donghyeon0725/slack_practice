package com.slack.slack.common.entity;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.slack.slack.common.dto.board.BoardDTO;
import com.slack.slack.common.code.ErrorCode;
import com.slack.slack.common.event.Events;
import com.slack.slack.common.event.events.BoardCreateEvent;
import com.slack.slack.common.exception.UnauthorizedException;
import com.slack.slack.common.code.State;
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

    private BaseCreateEntity baseCreateEntity;
    private BaseModifyEntity baseModifyEntity;

    public Board deletedByTeamMember(TeamMember member) {
        if (!this.team.equals(member.getTeam()))
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE);

        this.state = State.DELETED;
        this.baseModifyEntity = BaseModifyEntity.now(member.getUser().getEmail());
        return this;
    }

    public Board updatedByTeamMember(TeamMember member, BoardDTO boardDTO) {

        Team team = this.getTeam();

        User teamCreator = team.getUser();

        User boardCreator = this.getTeamMember().getUser();

        if (!member.getUser().equals(teamCreator) && !member.getUser().equals(boardCreator))
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE);

        this.state = State.UPDATED;
        this.title = boardDTO.getTitle();
        this.content = boardDTO.getContent();
        this.baseModifyEntity = BaseModifyEntity.now(member.getUser().getEmail());

        return this;
    }

    public Board bannerUpdatedByTeamMember(TeamMember member, String bannerPath) {
        Team team = this.getTeam();

        User teamCreator = team.getUser();

        User boardCreator = this.getTeamMember().getUser();

        if (!member.getUser().equals(teamCreator) && !member.getUser().equals(boardCreator))
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE);


        this.bannerPath = bannerPath;
        this.state = State.UPDATED;
        this.baseModifyEntity = BaseModifyEntity.now(member.getUser().getEmail());

        return this;
    }

    public void place() {
        Events.raise(new BoardCreateEvent(this));
        this.created();
    }

    public void created() {
        this.state = State.CREATED;
    }
}