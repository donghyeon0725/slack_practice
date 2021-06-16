package com.slack.slack.domain.team;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.slack.slack.domain.board.Board;
import com.slack.slack.domain.user.User;
import com.slack.slack.system.State;
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

}
