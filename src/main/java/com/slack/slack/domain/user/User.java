package com.slack.slack.domain.user;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.slack.slack.domain.card.*;
import com.slack.slack.domain.common.BaseCreateEntity;
import com.slack.slack.domain.common.BaseModifyEntity;
import com.slack.slack.domain.team.Team;
import com.slack.slack.domain.team.TeamChat;
import com.slack.slack.domain.team.TeamDTO;
import com.slack.slack.domain.team.TeamMember;
import com.slack.slack.error.exception.ErrorCode;
import com.slack.slack.error.exception.InvalidInputException;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
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
    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<UserRole> userRoles = new HashSet<>();

    private BaseCreateEntity baseCreateEntity;
    private BaseModifyEntity baseModifyEntity;


    public boolean passwordValidate(String password, PasswordEncoder passwordEncoder) throws InvalidInputException {
        if (!passwordEncoder.matches(password, this.password))
            throw new InvalidInputException(ErrorCode.WRONG_PASSWORD);

        return true;
    }

    public Team delete(Team team) {
        return team.deletedByUser(this);
    }

    public Card delete(Card card) {
        return card.deletedByUser(this);
    }

    public Attachment delete(Attachment attachment) {
        return attachment.deletedByUser(this);
    }

    public Team update(Team team, TeamDTO teamDTO) {
        return team.updatedByUser(this, teamDTO);
    }

    public Card update(Card card, CardDTO cardDTO) {
        return card.updatedByUser(this, cardDTO);
    }

    public Reply update(Reply reply, ReplyDTO replyDTO) {
        return reply.updatedByUser(this, replyDTO);
    }

    public Team patchUpdate(Team team, TeamDTO teamDTO) {
        return team.patchUpdatedByUser(this, teamDTO);
    }

    public TeamMember kickout(TeamMember member) {
        return member.kickedByUser(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof  User)) return false;
        User user = (User) o;
        return Objects.equals(getId(), user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
