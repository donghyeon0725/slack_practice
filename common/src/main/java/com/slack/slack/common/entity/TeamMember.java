package com.slack.slack.common.entity;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.slack.slack.common.code.Status;
import com.slack.slack.common.dto.board.BoardCommand;
import com.slack.slack.common.code.ErrorCode;
import com.slack.slack.common.event.Events;
import com.slack.slack.common.event.events.TeamMemberCreateEvent;
import com.slack.slack.common.exception.InvalidInputException;
import com.slack.slack.common.exception.UnauthorizedException;
import lombok.*;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@JsonFilter("TeamMember")
@Builder
@Where(clause = "status != 'DELETED'")
public class TeamMember {
    @Id
    @GeneratedValue
    private Integer teamMemberId;

    @JoinColumn(name = "team_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Team team;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToMany(mappedBy = "teamMember")
    private List<Board> boards;

    @OneToMany(mappedBy = "teamMember")
    private List<TeamActivity> teamActivities;

    @Enumerated(EnumType.STRING)
    private Status status;

    private Date date;

    @OneToMany(mappedBy = "teamMember")
    private List<Card> cards;

    @OneToMany(mappedBy = "teamMember")
    private List<Reply> replies;

    private BaseCreateEntity baseCreateEntity;
    private BaseModifyEntity baseModifyEntity;

    public TeamMember kickedByUser(User user) {
        // 팀 생성자가 아닌 경우
        if (!user.equals(team.getUser()))
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE);

        // 생성자가 자신을 강퇴하려는 경우
        if (user.equals(this.getUser()))
            throw new InvalidInputException(ErrorCode.INVALID_INPUT_VALUE);

        this.status = Status.KICKOUT;
        this.baseModifyEntity = BaseModifyEntity.now(user.getEmail());

        return this;
    }

    public void place() {
        Events.raise(new TeamMemberCreateEvent(this));
        this.created();
    }

    public void created() {
        this.status = Status.CREATED;
    }

    public void joined() {
        this.status = Status.JOIN;
    }

    public Board delete(Board board) {
        return board.deletedByTeamMember(this);
    }

    public Board update(Board board, BoardCommand boardCommand) {
        return board.updatedByTeamMember(this, boardCommand);
    }

    public Board updateBanner(Board board, String bannerPath) {
        return board.bannerUpdatedByTeamMember(this, bannerPath);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeamMember that = (TeamMember) o;
        return getTeamMemberId().equals(that.getTeamMemberId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTeamMemberId());
    }

}
