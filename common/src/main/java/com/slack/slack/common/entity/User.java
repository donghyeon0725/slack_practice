package com.slack.slack.common.entity;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.slack.slack.common.code.Status;
import com.slack.slack.common.dto.card.CardDTO;
import com.slack.slack.common.dto.card.ReplyDTO;
import com.slack.slack.common.dto.team.TeamDTO;
import com.slack.slack.common.code.ErrorCode;
import com.slack.slack.common.entity.validator.TeamValidator;
import com.slack.slack.common.entity.validator.UserValidator;
import com.slack.slack.common.exception.InvalidInputException;
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
    private Integer userId;

    @Column(name="email" , unique=true)
    private String email;

    private String password;

    private String name;

    @Enumerated(EnumType.STRING)
    private Status status;

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


    public boolean checkPassword(String password, PasswordEncoder passwordEncoder) throws InvalidInputException {
        if (!passwordEncoder.matches(password, this.password))
            throw new InvalidInputException(ErrorCode.WRONG_PASSWORD);

        return true;
    }

    public Team delete(Team team, TeamValidator validator) {
        return team.deletedByUser(this, validator);
    }

    public Card delete(Card card) {
        return card.deletedByUser(this);
    }

    public Attachment delete(Attachment attachment) {
        return attachment.deletedByUser(this);
    }

    public Team update(Team team, TeamDTO teamDTO, TeamValidator validator) {
        return team.updatedByUser(this, teamDTO, validator);
    }

    public Card update(Card card, CardDTO cardDTO) {
        return card.updatedByUser(this, cardDTO);
    }

    public Reply update(Reply reply, ReplyDTO replyDTO) {
        return reply.updatedByUser(this, replyDTO);
    }

    public Team patchUpdate(Team team, TeamDTO teamDTO, TeamValidator validator) {
        return team.patchUpdatedByUser(this, teamDTO, validator);
    }

    public TeamMember kickout(TeamMember member) {
        return member.kickedByUser(this);
    }

    public void created(UserValidator validator) {
        validator.validateUserForCreate(this.email);
        this.status = Status.CREATED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof  User)) return false;
        User user = (User) o;
        return Objects.equals(getUserId(), user.getUserId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserId());
    }
}
