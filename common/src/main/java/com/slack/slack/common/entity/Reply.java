package com.slack.slack.common.entity;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.slack.slack.common.dto.card.ReplyCommand;
import lombok.*;

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
    private Integer replyId;

    @JoinColumn(name = "card_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Card card;

    @JoinColumn(name = "team_member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private TeamMember teamMember;

    @OneToMany(mappedBy = "reply")
    private List<TeamActivity> teamActivities;

    private String content;

    private Date date;

    private BaseCreateEntity baseCreateEntity;
    private BaseModifyEntity baseModifyEntity;

    public Reply updatedByUser(User user, ReplyCommand replyCommand) {
        this.content = replyCommand.getContent();
        this.baseModifyEntity = BaseModifyEntity.now(user.getEmail());

        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof Reply)) return false;
        Reply reply = (Reply) o;
        return Objects.equals(getReplyId(), reply.getReplyId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getReplyId());
    }

}
