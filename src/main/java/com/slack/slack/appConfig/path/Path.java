package com.slack.slack.appConfig.path;

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
