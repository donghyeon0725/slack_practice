package com.slack.slack.system;


import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Code {
    COMPLETE(200, "동작 완료");
    private Integer code;
    private String description;
}
