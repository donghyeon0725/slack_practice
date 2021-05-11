package com.slack.slack.domain.user;

import lombok.Data;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
public class UserDTO {
    @NotNull(message = "이메일은 필수 값 입니다.")    // null은 안됨
    @NotBlank(message = "이메일은 필수 값 입니다.")   // 빈문자열은 안됨
    private String email;

    @NotBlank
    @NotNull
    private String name;

    @Size(min=8, message = "비밀번호는 최소 8자리 이상이어야 합니다")
    private String password;

    @Past
    private Date date;
}
