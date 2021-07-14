package com.slack.slack.appConfig.security.form.service;

import com.slack.slack.appConfig.security.domain.repository.AccountRepository;
import com.slack.slack.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AccountDetailsService implements UserDetailsService {

    private final AccountRepository repository;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return repository.findByUsername(s).map(account -> new AccountContext(account)).orElseThrow(() -> new UsernameNotFoundException(ErrorCode.RESOURCE_NOT_FOUND.getMessage()));
    }

}