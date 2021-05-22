package com.slack.slack.system;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Role {
    ROLE_USER("ROLE_USER"),
    ROLE_ADMIN("ROLE_ADMIN");

    private String role;

    public String getRole() {
        return role;
    }
}
