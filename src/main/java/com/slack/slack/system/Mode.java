package com.slack.slack.system;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Mode {
    DEV("dev"), PRODUCT("product"), LOCAL("local");
    private String mode;
}
