package com.slack.slack.domain.card;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@ApiModel(description = "카드")
public class CardDTO {
    private Integer id;

    private Integer teamMemberId;

    private Integer boardId;

    @ApiModelProperty(notes = "제목", example = "카드 제목")
    @NotNull(message = "제목은 필수 값 입니다.")
    @NotBlank(message = "제목은 필수 값 입니다.")
    private String title;

    @ApiModelProperty(notes = "카드 글", example = "카드 내용")
    @Size(max = 2000, message = "글은 최대 2000자까지 가능합니다.")
    private String content;

    @ApiModelProperty(notes = "위치")
    private Integer position;
}
