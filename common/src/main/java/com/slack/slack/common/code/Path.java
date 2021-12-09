package com.slack.slack.common.code;

public enum Path {
    ALL("/*");


    private String type;

    Path(String type) {
        this.type = type;
    }

    public String get() {
        return this.type;
    }
}
