package com.slack.slack.domain.card;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(description = "첨부 파일")
public class AttachmentDTO {

    private Integer id;

    private Integer cardId;
}
