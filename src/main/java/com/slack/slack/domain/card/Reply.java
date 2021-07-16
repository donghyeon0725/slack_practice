package com.slack.slack.domain.card;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.slack.slack.domain.common.BaseCreateEntity;
import com.slack.slack.domain.common.BaseModifyEntity;
import com.slack.slack.domain.team.Team;
import com.slack.slack.domain.team.TeamActivity;
import com.slack.slack.domain.team.TeamMember;
import com.slack.slack.domain.user.User;
import com.slack.slack.error.exception.ErrorCode;
import com.slack.slack.error.exception.UnauthorizedException;
import lombok.*;
import org.hibernate.annotations.Target;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@JsonFilter("Reply")
@Builder
public class Reply {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Card card;

    @ManyToOne(fetch = FetchType.LAZY)
    private TeamMember teamMember;

    @OneToMany(mappedBy = "reply")
    private List<TeamActivity> teamActivities;

    private String content;

    private Date date;

    private BaseCreateEntity baseCreateEntity;
    private BaseModifyEntity baseModifyEntity;

    public Reply updatedByUser(User user, ReplyDTO replyDTO) {
        this.content = replyDTO.getContent();
        this.baseModifyEntity = BaseModifyEntity.now(user.getEmail());

        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof Reply)) return false;
        Reply reply = (Reply) o;
        return Objects.equals(getId(), reply.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

}
