package com.slack.slack.common.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@ApiModel(description = "로그인할 때 사용할 사용자 정보")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class LoginUserCommand {

    @Email
    @ApiModelProperty(notes = "이메일 주소")
    @NotNull(message = "이메일은 필수 값 입니다.")    // null은 안됨
    @NotBlank(message = "이메일은 필수 값 입니다.")   // 빈문자열은 안됨
    private String email;

    @ApiModelProperty(notes = "비밀번호")
    @Size(min=8, message = "비밀번호는 최소 8자리 이상이어야 합니다")
    private String password;


}
