package com.slack.slack.domain.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;

import javax.validation.constraints.*;
import java.util.Date;

@Data
@ApiModel(description = "사용자 정보")
public class UserDTO {

    @Email
    @ApiModelProperty(notes = "이메일 주소", example = "ehdgus5015@gmail.com")
    @NotNull(message = "이메일은 필수 값 입니다.")    // null은 안됨
    @NotBlank(message = "이메일은 필수 값 입니다.")   // 빈문자열은 안됨
    private String email;

    @ApiModelProperty(notes = "사용자 이름", example = "기면지")
    @NotBlank
    @NotNull
    private String name;

    @ApiModelProperty(notes = "비밀번호", example = "#rifk9r09")
    @Size(min=8, message = "비밀번호는 최소 8자리 이상이어야 합니다")
    private String password;

    @ApiModelProperty(notes = "날짜", example = "2021-06-01T09:51:18")
    @Past
    private Date date;
}
