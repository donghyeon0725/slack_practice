package com.slack.slack.common.code;

public enum Encoding {
    UTF8("UTF-8");

    private String type;

    Encoding(String type) {
        this.type = type;
    }

    public String get() {
        return this.type;
    }

}
