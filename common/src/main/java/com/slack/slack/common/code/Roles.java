package com.slack.slack.common.code;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Roles {
    ROLE_USER("ROLE_USER"),
    ROLE_ADMIN("ROLE_ADMIN");

    private String role;

    public String getRole() {
        return role;
    }
}
