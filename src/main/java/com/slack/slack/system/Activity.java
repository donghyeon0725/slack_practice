package com.slack.slack.system;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Activity {
    TEAM_CREATED("A001", "팀이 생성되었습니다")
    ,BOARD_CREATED("A002", "보드가 생성되었습니다")
    ,CARD_CREATED("A003", "카드가 생성되었습니다");

    private String id;
    private String detail;

    public String getDetail() {
        return this.detail;
    }
}
