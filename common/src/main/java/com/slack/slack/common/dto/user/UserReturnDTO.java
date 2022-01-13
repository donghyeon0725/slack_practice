package com.slack.slack.common.dto.user;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class UserReturnDTO {
    private Integer userId;

    private String email;

    private String name;

    private String status;

    private Date date;
}
