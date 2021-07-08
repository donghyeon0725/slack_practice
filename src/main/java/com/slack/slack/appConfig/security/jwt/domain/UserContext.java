package com.slack.slack.appConfig.security.jwt.domain;

import com.slack.slack.appConfig.security.common.domain.Role;
import com.slack.slack.domain.user.User;
import com.slack.slack.domain.user.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Entity User 와
 * UserDetails의 호환을 위한 클래스
 * */
public class UserContext extends org.springframework.security.core.userdetails.User {
    private User user;

    public UserContext(User user) {
        super(user.getEmail()
                , user.getPassword()
                // 권한으로 부터 SimpleGrantedAuthority 객체 추출
                , user.getUserRoles().stream()
                        .map(UserRole::getRole)
                        .map(Role::getRoleName)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList())
        );
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
