package com.slack.slack.system;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum State {

    INVITED("S001", "초대 상태입니다."),
    JOIN("S002", "맴버 입니다."),
    CREATED("S003", "생성 되었습니다"),
    DELETED("S004", "삭제 되었습니다"),
    UPDATED("S005", "업데이트 되었습니다"),
    KICKOUT("S006", "추방 되었습니다."),
    CREATOR("S007", "팀 생성자입니다."),
    MEMBER("S008", "팀 멤버입니다."),
    BOARD_CREATOR("S009", "보드 생성자입니다"),
    NO_AUTH("S010", "권한이 없습니다"),
    CARD_CREATOR("S011", "카드 생성자 입니다.");


    private String id;
    private String description;
}
