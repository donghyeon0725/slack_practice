package com.slack.slack.common.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.time.LocalDateTime;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class BaseModifyEntity {
    private String modifier;
    private LocalDateTime modifiedAt;

    public static BaseModifyEntity now(String modifier) {
        return new BaseModifyEntity(modifier, LocalDateTime.now());
    }
}
