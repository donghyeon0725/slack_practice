package com.slack.slack.domain.user;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.slack.slack.domain.common.BaseCreateEntity;
import com.slack.slack.domain.common.BaseModifyEntity;
import com.slack.slack.domain.team.Team;
import com.slack.slack.domain.team.TeamChat;
import com.slack.slack.domain.team.TeamMember;
import lombok.*;
import org.hibernate.annotations.Target;

import javax.persistence.*;
import java.util.*;

// 유지보수의 용이성을 위해 AccessLevel은 PROTECTED로 변경하고 Setter는 제거
@AllArgsConstructor
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@JsonFilter("User")
public class User {
    @Id
    @GeneratedValue
    private Integer id;

    private String email;

    private String password;

    private String name;

    private String state;

    private Date date;

    @OneToMany(mappedBy = "user")
    private List<Team> team;

    @OneToMany(mappedBy = "team")
    private List<TeamChat> teamChats;

    @OneToMany(mappedBy = "user")
    private List<TeamMember> teamMember;

    @Builder
    public User(Integer id, String email, String password, String name, String state, Date date, List<Team> team, List<TeamChat> teamChats, List<TeamMember> teamMember, BaseCreateEntity baseCreateEntity, BaseModifyEntity baseModifyEntity) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.state = state;
        this.date = date;
        this.team = team;
        this.teamChats = teamChats;
        this.teamMember = teamMember;
        this.baseCreateEntity = baseCreateEntity;
        this.baseModifyEntity = baseModifyEntity;
    }

    /**
     * jwt 사용을 위함
     * 이 유저가 가진 권한의 목록을 저장하는 필드
     * */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<UserRole> userRoles = new HashSet<>();

    private BaseCreateEntity baseCreateEntity;
    private BaseModifyEntity baseModifyEntity;

}
