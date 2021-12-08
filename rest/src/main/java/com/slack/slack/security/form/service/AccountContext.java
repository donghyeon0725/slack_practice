package com.slack.slack.security.form.service;

import com.slack.slack.common.entity.Account;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.stream.Collectors;

/**
 * Account 의 wrapper 로 UserDetails 와 호환을 위한 클래스
 * */
public class AccountContext extends User {

    private static final long serialVersionUID = 6529685098267757690L;

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
