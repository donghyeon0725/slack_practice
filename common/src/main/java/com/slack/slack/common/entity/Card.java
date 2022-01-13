package com.slack.slack.common.entity;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.slack.slack.common.code.Status;
import com.slack.slack.common.dto.card.CardDTO;
import com.slack.slack.common.code.ErrorCode;
import com.slack.slack.common.entity.validator.CardValidator;
import com.slack.slack.common.event.Events;
import com.slack.slack.common.event.events.CardAddEvent;
import com.slack.slack.common.exception.UnauthorizedException;
import lombok.*;
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
@Where(clause = "status != 'DELETED'")
public class Card {
    @Id
    @GeneratedValue
    private Integer cardId;

    @OneToMany(mappedBy = "card")
    private List<TeamActivity> teamActivities;

    @JoinColumn(name = "team_member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private TeamMember teamMember;

    @JoinColumn(name = "board_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;

    private String name;

    private String content;

    private Integer position;

    @Enumerated(EnumType.STRING)
    private Status status;

    private Date date;

    @Builder.Default
    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reply> replies = new ArrayList<>();

    @Builder.Default
    @Where(clause = "status != 'DELETED'")
    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attachment> attachments = new ArrayList<>();

    @Transient
    private boolean isSelected = false;

    private BaseCreateEntity baseCreateEntity;
    private BaseModifyEntity baseModifyEntity;

    public Card deletedByUser(User user, CardValidator cardValidator) {
        cardValidator.checkTeamOwnerOrBoardOwnerOrCardOwner(board.getTeam(), board, this, user);

        this.status = Status.DELETED;
        this.baseModifyEntity = BaseModifyEntity.now(user.getEmail());
        this.attachments.clear();
        return this;
    }


    // 권한 검사, 상태 변화
    public Card updatedByUser(User user, CardDTO cardDTO, CardValidator validator) {
        validator.checkCardOwner(this, user);

        this.name = cardDTO.getName();
        this.content = cardDTO.getContent();
        this.baseModifyEntity = BaseModifyEntity.now(user.getEmail());
        this.status = Status.UPDATED;

        return this;
    }

    public Card changePosition(Integer position) {
        this.position = position;
        return this;
    }

    public void created() {
        this.status = Status.CREATED;

        Events.raise(new CardAddEvent(board.getTeam(), this));
    }

}
