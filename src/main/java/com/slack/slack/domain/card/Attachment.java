package com.slack.slack.domain.card;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.slack.slack.domain.team.TeamActivity;
import com.slack.slack.system.State;
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
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Card card;

    private String path;

    private String systemFilename;

    private String extension;

    private String filename;

    private Long size;

    private String description;

    private Date date;

    @Enumerated(EnumType.STRING)
    private State state;

    @OneToMany(mappedBy = "attachment")
    private List<TeamActivity> teamActivities;
}
