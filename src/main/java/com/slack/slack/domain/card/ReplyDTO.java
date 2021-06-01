package com.slack.slack.domain.card;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@ApiModel(description = "댓글")
public class ReplyDTO {

    private Integer id;

    @ApiModelProperty(notes = "카드 아이디", example = "4")
    @NotNull(message = "카드 아이디는 필수 값 입니다.")
    @NotBlank(message = "카드 아이디는 필수 값 입니다.")
    private Integer cardId;

    @ApiModelProperty(notes = "팀 멤버 아이디", example = "3")
    @NotNull(message = "팀 멤버 아이디는 필수 값 입니다.")
    @NotBlank(message = "팀 멤버 아이디는 필수 값 입니다.")
    private Integer teamMemberId;

    @ApiModelProperty(notes = "내용", example = "댓글 입니다")
    @NotNull(message = "글 내용은 필수 값 입니다.")
    @NotBlank(message = "글 내용은 필수 값 입니다.")
    private String content;

    private Date date;
}
