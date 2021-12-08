package com.slack.slack.common.code;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Mode {
    DEV("dev"), PRODUCT("product"), LOCAL("local");
    private String mode;
}
