package com.slack.slack.common.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.time.LocalDateTime;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class BaseCreateEntity {
    private String creator;
    private LocalDateTime createdAt;

    public static BaseCreateEntity now(String creator) {
        return new BaseCreateEntity(creator, LocalDateTime.now());
    }
}
