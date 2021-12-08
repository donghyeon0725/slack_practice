package com.slack.slack.common.entity;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.slack.slack.common.dto.board.BoardDTO;
import com.slack.slack.common.code.ErrorCode;
import com.slack.slack.common.error.exception.InvalidInputException;
import com.slack.slack.common.error.exception.UnauthorizedException;
import com.slack.slack.common.code.State;
import lombok.*;
import org.hibernate.annotations.Where;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@JsonFilter("TeamMember")
@Builder
@Where(clause = "state != 'DELETED'")
public class TeamMember {
    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToMany(mappedBy = "teamMember")
    private List<Board> boards;

    @OneToMany(mappedBy = "teamMember")
    private List<TeamActivity> teamActivities;

    @NonNull
    @Enumerated(EnumType.STRING)
    private State state;

    private Date date;

    @OneToMany(mappedBy = "teamMember")
    private List<Card> cards;

    @OneToMany(mappedBy = "teamMember")
    private List<Reply> replies;

    private BaseCreateEntity baseCreateEntity;
    private BaseModifyEntity baseModifyEntity;

    public TeamMember kickedByUser(User user) {
        // 팀 생성자가 아닌 경우
        if (!user.equals(team.getUser()))
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED_VALUE);

        // 생성자가 자신을 강퇴하려는 경우
        if (user.equals(this.getUser()))
            throw new InvalidInputException(ErrorCode.INVALID_INPUT_VALUE);

        this.state = State.KICKOUT;
        this.baseModifyEntity = BaseModifyEntity.now(user.getEmail());

        return this;
    }

    public Board delete(Board board) {
        return board.deletedByTeamMember(this);
    }

    public Board update(Board board, BoardDTO boardDTO) {
        return board.updatedByTeamMember(this, boardDTO);
    }

    public Board updateBanner(Board board, String bannerPath) {
        return board.bannerUpdatedByTeamMember(this, bannerPath);
    }

}
