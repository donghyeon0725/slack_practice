package com.slack.slack.domain.card;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.slack.slack.domain.board.Board;
import com.slack.slack.domain.common.BaseCreateEntity;
import com.slack.slack.domain.common.BaseModifyEntity;
import com.slack.slack.domain.team.TeamActivity;
import com.slack.slack.domain.team.TeamMember;
import com.slack.slack.domain.user.User;
import com.slack.slack.error.exception.ErrorCode;
import com.slack.slack.error.exception.UnauthorizedException;
import com.slack.slack.system.State;
import lombok.*;
import org.hibernate.annotations.Target;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@JsonFilter("Card")
@Builder
@Where(clause = "state != 'DELETED'")
public class Card {
    @Id
    @GeneratedValue
    private Integer id;

    @OneToMany(mappedBy = "card")
    private List<TeamActivity> teamActivities;

    @ManyToOne(fetch = FetchType.LAZY)
    private TeamMember teamMember;

    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;

    private String title;

    private String content;

    private Integer position;

    @Enumerated(EnumType.STRING)
    private State state;

    private Date date;

    @Builder.Default
    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reply> replies = new ArrayList<>();

    @Builder.Default
    @Where(clause = "state != 'DELETED'")
    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attachment> attachments = new ArrayList<>();

    @Transient
    private boolean isSelected = false;

    private BaseCreateEntity baseCreateEntity;
    private BaseModifyEntity baseModifyEntity;

    public Card deletedByUser(User user) {
        this.state = State.DELETED;
        this.baseModifyEntity = BaseModifyEntity.now(user.getEmail());
        this.attachments.clear();
        return this;
    }


    // 권한 검사, 상태 변화
    public Card updatedByUser(User user, CardDTO cardDTO) {

        if (!user.equals(this.getTeamMember().getUser()))
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE);

        this.title = cardDTO.getTitle();
        this.content = cardDTO.getContent();
        this.baseModifyEntity = BaseModifyEntity.now(user.getEmail());
        this.state = State.UPDATED;

        return this;
    }

    public Card changePosition(Integer position) {
        this.position = position;
        return this;
    }

}
