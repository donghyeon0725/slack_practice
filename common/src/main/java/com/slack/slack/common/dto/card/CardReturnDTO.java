package com.slack.slack.common.dto.card;

import com.slack.slack.common.code.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class CardReturnDTO {

    private Integer cardId;

    private String name;

    private String content;

    private Integer position;

    private Status status;

    private Date date;

    private List<ReplyReturnDTO> replies;

    private List<AttachmentReturnDTO> attachments;


    private boolean isSelected = false;
}
