package com.slack.slack.domain.team;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.slack.slack.system.Activity;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@AllArgsConstructor
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@JsonFilter("TeamActivity")
@Builder
public class TeamActivity {
    @Id
    @GeneratedValue
    private Integer id;

    private Integer boardId;

    private Integer teamMemberId;

    private Integer cardId;

    private Integer replyId;

    private Integer attachmentId;

    @Enumerated(EnumType.STRING)
    private Activity detail;

    private Date date;
}
