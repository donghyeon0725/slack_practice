package com.slack.slack.common.entity;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.slack.slack.common.dto.team.TeamDTO;
import com.slack.slack.common.repository.TeamRepository;
import com.slack.slack.common.code.ErrorCode;
import com.slack.slack.error.exception.ResourceConflict;
import com.slack.slack.error.exception.UnauthorizedException;
import com.slack.slack.common.code.State;
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
@Where(clause = "state != 'DELETED'")
public class Team {

    @Id
    @GeneratedValue
    private Integer id;

    // 레이지 로딩
    // Entity관의 sub와 main관계를 설정하지 않으면 에러가 남. Main : Sub = Parent : Child
    // 하나의 엔티티에 매핑이 됨. 즉, sub 타입임
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private State state;

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

    public static boolean duplicationCheck(User user, TeamRepository teamRepository) {
        List<Team> teams = teamRepository.findByUser(user).get();
        if (teams.size() > 0)
            throw new ResourceConflict(ErrorCode.RESOURCE_CONFLICT);

        return true;
    }

    public Team deletedByUser(User modifier) {
        this.checkAuth(modifier);

        this.state = State.DELETED;
        baseModifyEntity = BaseModifyEntity.now(modifier.getEmail());
        return this;
    }

    public Team updatedByUser(User modifier, TeamDTO teamDTO) {
        this.checkAuth(modifier);

        this.name = teamDTO.getName();
        this.description = teamDTO.getDescription();
        this.state = State.UPDATED;
        this.baseModifyEntity = BaseModifyEntity.now(this.user.getEmail());

        return this;
    }

    public Team patchUpdatedByUser(User modifier, TeamDTO teamDTO) {
        this.checkAuth(modifier);

        if (teamDTO.getName() != null)
            this.name = teamDTO.getName();
        if (teamDTO.getDescription() != null)
            this.description = teamDTO.getDescription();
        this.state = State.UPDATED;
        this.baseModifyEntity = BaseModifyEntity.now(this.user.getEmail());

        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof Team))  return false;
        Team team = (Team) o;
        return Objects.equals(getId(), team.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
