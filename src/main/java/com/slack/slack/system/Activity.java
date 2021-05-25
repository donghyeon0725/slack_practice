package com.slack.slack.system;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Activity {
    TEAM_CREATED("A001", "팀이 생성되었습니다");

    private String id;
    private String detail;
}
