package com.slack.slack.common.dto.card;

import com.slack.slack.common.code.Status;
import com.slack.slack.common.entity.Card;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CardDTO {

    private Integer cardId;

    private String name;

    private String content;

    private Integer position;

    private Status status;

    private Date date;

    private List<ReplyDTO> replies;

    private List<AttachmentDTO> attachments;


    private boolean isSelected = false;

    public CardDTO(Card card) {
        BeanUtils.copyProperties(card, this);
    }
}
