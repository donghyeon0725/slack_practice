package com.slack.slack.common.dto.team;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.util.Date;

@Data
@ApiModel(description = "팀 정보")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamCommand {

    private Integer id;

    @ApiModelProperty(notes = "팀명", example = "이전")
    @NotNull(message = "팀명은 필수 값 입니다.")    // null은 안됨
    @NotBlank(message = "팀명은 필수 값 입니다.")   // 빈문자열은 안됨
    private String name;

    @ApiModelProperty(notes = "팀 설명", example = "우리 팀은 좋은 팀입니다")
    @Size(max=2000, message = "팀 설명은 최대 2000자를 넘을 수 없습니다.")
    private String description;

    @ApiModelProperty(notes = "날짜")
    @Past
    private Date date;

}
