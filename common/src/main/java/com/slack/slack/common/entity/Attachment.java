package com.slack.slack.common.entity;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.slack.slack.common.embedded.AttachedFile;
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
@JsonFilter("Attachment")
@Builder
@Where(clause = "state != 'DELETED'")
public class Attachment {
    @Id
    @GeneratedValue
    private Integer attachmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    private Card card;

    private AttachedFile attachedFile;

    private String description;

    private Date date;

    @Enumerated(EnumType.STRING)
    private State state;

    @OneToMany(mappedBy = "attachment")
    private List<TeamActivity> teamActivities;

    private BaseCreateEntity baseCreateEntity;
    private BaseModifyEntity baseModifyEntity;

    public Attachment deletedByUser(User user) {
        this.state = State.DELETED;
        this.baseModifyEntity = BaseModifyEntity.now(user.getEmail());

        return this;
    }
}
