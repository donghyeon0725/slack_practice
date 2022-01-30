package com.slack.slack.common.dto.user;

import com.slack.slack.common.entity.User;
import com.slack.slack.common.util.ModelMapperUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class UserDTO {
    private Integer userId;

    private String email;

    private String name;

    private String status;

    private Date date;

    public UserDTO(User user) {
        BeanUtils.copyProperties(user, this);
    }
}
