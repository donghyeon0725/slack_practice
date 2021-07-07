package com.slack.slack.appConfig.security.service;

import com.slack.slack.appConfig.security.domain.Account;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.stream.Collectors;

/**
 * Account 의 wrapper 로 UserDetails 와 호환을 위한 클래스
 * */
public class AccountContext extends User {

    private final Account account;

    public AccountContext(Account account) {
        super(account.getUsername(), account.getPassword(),
                account.getAccountRoles().stream().map(s -> new SimpleGrantedAuthority(s.getRole().getRoleName())).collect(Collectors.toList()));
        this.account = account;
    }

    // GrantedAuthority
    public Account getAccount() {
        return account;
    }
}
