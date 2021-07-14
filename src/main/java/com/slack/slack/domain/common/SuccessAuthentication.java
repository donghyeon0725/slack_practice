package com.slack.slack.domain.common;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SuccessAuthentication {
    public static Authentication getAuth() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static <T> T getPrincipal(Class<T> clazz) {
        return (T)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
