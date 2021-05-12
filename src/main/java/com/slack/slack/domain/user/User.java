package com.slack.slack.domain.user;

import com.fasterxml.jackson.annotation.JsonFilter;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.util.Date;

// 유지보수의 용이성을 위해 AccessLevel은 PROTECTED로 변경하고 Setter는 제거
@AllArgsConstructor
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@JsonFilter("User")
public class User {
    @Id
    @GeneratedValue
    private Integer id;

    private String email;

    private String password;

    private String name;

    private String Status;

    @Past // 과거 날짜여야 합니다.
    private Date date;

}
