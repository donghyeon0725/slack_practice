package com.slack.slack.system;


import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Code {
    COMPLETE(200, "동작 완료"), FAIL(400, "동작 실패");
    private Integer code;
    private String description;

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
