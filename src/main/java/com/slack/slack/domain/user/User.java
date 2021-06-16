package com.slack.slack.domain.user;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.slack.slack.domain.team.Team;
import com.slack.slack.domain.team.TeamChat;
import com.slack.slack.domain.team.TeamMember;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

// 유지보수의 용이성을 위해 AccessLevel은 PROTECTED로 변경하고 Setter는 제거
@AllArgsConstructor
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@JsonFilter("User")
@Builder
public class User implements UserDetails {
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
     *
     * 이 유저가 가진 권한의 목록을 저장하는 필드
     * */
    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> roles = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return id.toString();
    }

    // 계정 정보가 만료 되진 않았는지
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정이 잠기지 않았는지
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 인증 정보가 만료 되지 않았는지
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 수정 가능한지?
    @Override
    public boolean isEnabled() {
        return true;
    }
}
