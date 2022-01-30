package com.slack.slack.common.entity;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.slack.slack.common.code.Status;
import com.slack.slack.common.dto.board.BoardCommand;
import com.slack.slack.common.code.ErrorCode;
import com.slack.slack.common.event.Events;
import com.slack.slack.common.event.events.BoardCreateEvent;
import com.slack.slack.common.exception.UnauthorizedException;
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
@Where(clause = "status != 'DELETED'")
public class Board {

    @Id
    @GeneratedValue
    private Integer boardId;

    @JoinColumn(name = "team_member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private TeamMember teamMember;

    @JoinColumn(name = "team_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Team team;

    private String name;

    private String content;

    private String bannerPath;

    @Enumerated(EnumType.STRING)
    private Status status;

    private Date date;

    @OneToMany(mappedBy = "board")
    private List<TeamActivity> teamActivities;

    @Where(clause = "status != 'DELETED'")
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

        this.status = Status.DELETED;
        this.baseModifyEntity = BaseModifyEntity.now(member.getUser().getEmail());
        return this;
    }

    public Board updatedByTeamMember(TeamMember member, BoardCommand boardCommand) {

        Team team = this.getTeam();

        User teamCreator = team.getUser();

        User boardCreator = this.getTeamMember().getUser();

        if (!member.getUser().equals(teamCreator) && !member.getUser().equals(boardCreator))
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE);

        this.status = Status.UPDATED;
        this.name = boardCommand.getTitle();
        this.content = boardCommand.getContent();
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
        this.status = Status.UPDATED;
        this.baseModifyEntity = BaseModifyEntity.now(member.getUser().getEmail());

        return this;
    }

    public void place() {
        Events.raise(new BoardCreateEvent(this));
        this.created();
    }

    public void created() {
        this.status = Status.CREATED;
    }
}
