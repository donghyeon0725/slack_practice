package com.slack.slack.common.entity;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.slack.slack.common.code.Status;
import com.slack.slack.common.event.Events;
import com.slack.slack.common.socket.event.events.TeamChatUpdateEvent;
import com.slack.slack.common.socket.event.events.TeamChatAddEvent;
import lombok.*;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Date;

@AllArgsConstructor
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder
@JsonFilter("TeamChat")
@Where(clause = "status != 'DELETED'")
public class TeamChat {
    @Id
    @GeneratedValue
    private Integer teamChatId;

    private String email;

    private String description;

    @Enumerated(EnumType.STRING)
    private Status status;

    private Date date;

    @ManyToOne(fetch = FetchType.LAZY)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private BaseCreateEntity baseCreateEntity;
    private BaseModifyEntity baseModifyEntity;

    public TeamChat delete() {
        this.status = Status.DELETED;
        this.description = Status.DELETED.getDescription();
        this.baseModifyEntity = BaseModifyEntity.now(this.email);

        Events.raise(new TeamChatUpdateEvent(this));
        return this;
    }

    public void place() {
        Events.raise(new TeamChatAddEvent(this));
        this.status = Status.CREATED;
    }
}
