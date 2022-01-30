package com.slack.slack.common.entity;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.slack.slack.common.code.Status;
import com.slack.slack.common.dto.team.TeamCommand;
import com.slack.slack.common.code.ErrorCode;
import com.slack.slack.common.entity.validator.TeamValidator;
import com.slack.slack.common.exception.UnauthorizedException;
import lombok.*;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.Past;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@JsonFilter("Team")
@Builder
@Where(clause = "status != 'DELETED'")
public class Team {

    @Id
    @GeneratedValue
    private Integer teamId;

    // 레이지 로딩
    // Entity관의 sub와 main관계를 설정하지 않으면 에러가 남. Main : Sub = Parent : Child
    // 하나의 엔티티에 매핑이 됨. 즉, sub 타입임
    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Past
    private Date date;

    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY)
    private List<TeamMember> teamMember;

    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY)
    private List<Board> boards;

    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY)
    private List<TeamChat> teamChats;

    private BaseCreateEntity baseCreateEntity;
    private BaseModifyEntity baseModifyEntity;

    private boolean checkAuth(User modifier) {
        if (!this.user.equals(modifier))
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE);
        return true;
    }

    public Team deletedByUser(User modifier, TeamValidator validator) {
        validator.checkTeamOwner(this, modifier);

        this.status = Status.DELETED;
        baseModifyEntity = BaseModifyEntity.now(modifier.getEmail());
        return this;
    }

    public Team updatedByUser(User modifier, TeamCommand teamCommand, TeamValidator validator) {
        validator.checkTeamOwner(this, modifier);

        this.name = teamCommand.getName();
        this.description = teamCommand.getDescription();
        this.status = Status.UPDATED;
        this.baseModifyEntity = BaseModifyEntity.now(this.user.getEmail());

        return this;
    }

    public Team patchUpdatedByUser(User modifier, TeamCommand teamCommand, TeamValidator validator) {
        validator.checkTeamOwner(this, modifier);

        if (teamCommand.getName() != null)
            this.name = teamCommand.getName();
        if (teamCommand.getDescription() != null)
            this.description = teamCommand.getDescription();
        this.status = Status.UPDATED;
        this.baseModifyEntity = BaseModifyEntity.now(this.user.getEmail());

        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof Team))  return false;
        Team team = (Team) o;
        return Objects.equals(getTeamId(), team.getTeamId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTeamId());
    }
}
