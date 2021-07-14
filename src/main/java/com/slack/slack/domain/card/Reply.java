package com.slack.slack.domain.card;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.slack.slack.domain.common.BaseCreateEntity;
import com.slack.slack.domain.common.BaseModifyEntity;
import com.slack.slack.domain.team.TeamActivity;
import com.slack.slack.domain.team.TeamMember;
import lombok.*;
import org.hibernate.annotations.Target;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

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
}
