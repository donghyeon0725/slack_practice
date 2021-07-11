package com.slack.slack.domain.user;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.slack.slack.domain.team.Team;
import com.slack.slack.domain.team.TeamChat;
import com.slack.slack.domain.team.TeamMember;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Past;
import java.util.*;

// 유지보수의 용이성을 위해 AccessLevel은 PROTECTED로 변경하고 Setter는 제거
@AllArgsConstructor
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@JsonFilter("User")
@Builder
public class User {
    @Id
    @GeneratedValue
    private Integer id;

    private String email;

    private String password;

    private String name;

    private String state;

    @Past // 과거 날짜여야 합니다.
    private Date date;

    @OneToMany(mappedBy = "user")
    private List<Team> team;

    @OneToMany(mappedBy = "team")
    private List<TeamChat> teamChats;

    @OneToMany(mappedBy = "user")
    private List<TeamMember> teamMember;


    /**
     * jwt 사용을 위함
     * 이 유저가 가진 권한의 목록을 저장하는 필드
     * */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<UserRole> userRoles = new HashSet<>();

}
