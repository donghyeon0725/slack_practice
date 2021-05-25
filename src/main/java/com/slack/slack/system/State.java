package com.slack.slack.system;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum State {

    INVITED("S001", "초대 상태입니다."),
    JOIN("S002", "맴버 입니다."),
    CREATED("S003", "생성 되었습니다"),
    DELETED("S004", "삭제 되었습니다"),
    UPDATED("S005", "업데이트 되었습니다");

    private String id;
    private String description;
}
