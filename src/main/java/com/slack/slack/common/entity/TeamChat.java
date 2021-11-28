package com.slack.slack.common.entity;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.slack.slack.common.code.State;
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
@Where(clause = "state != 'DELETED'")
public class TeamChat {
    @Id
    @GeneratedValue
    private Integer id;

    private String email;

    private String description;

    @Enumerated(EnumType.STRING)
    private State state;

    private Date date;

    @ManyToOne(fetch = FetchType.LAZY)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private BaseCreateEntity baseCreateEntity;
    private BaseModifyEntity baseModifyEntity;

    public TeamChat delete() {
        this.state = State.DELETED;
        this.baseModifyEntity = BaseModifyEntity.now(this.email);
        return this;
    }

}
