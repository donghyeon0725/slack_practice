package com.slack.slack.common.dto.board;

import com.slack.slack.common.code.ErrorCode;
import com.slack.slack.common.exception.InvalidInputException;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@ApiModel(description = "보드")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardDTO {
    private Integer id;

    private Integer teamMemberId;

    private Integer teamId;

    @ApiModelProperty(notes = "제목", example = "제목")
    @NotNull(message = "제목은 필수 값 입니다.")
    @NotBlank(message = "제목은 필수 값 입니다.")
    private String title;

    @ApiModelProperty(notes = "글", example = "글 내용")
    @Size(max = 2000, message = "글은 최대 2000자까지 가능합니다.")
    private String content;


    public boolean checkValidation() throws InvalidInputException {
        if (this.id == null)
            throw new InvalidInputException(ErrorCode.INVALID_INPUT_VALUE);

        return true;
    }
}
