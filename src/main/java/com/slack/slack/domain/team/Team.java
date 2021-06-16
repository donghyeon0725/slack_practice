package com.slack.slack.domain.team;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.slack.slack.domain.board.Board;
import com.slack.slack.domain.user.User;
import com.slack.slack.system.State;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Past;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@JsonFilter("Team")
@Builder
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

    @OneToMany(mappedBy = "team")
    private List<TeamMember> teamMember;

    @OneToMany(mappedBy = "team")
    private List<Board> boards;

    @OneToMany(mappedBy = "team")
    private List<TeamChat> teamChats;

}
