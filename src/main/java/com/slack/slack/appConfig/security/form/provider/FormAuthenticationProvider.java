package com.slack.slack.appConfig.security.form.provider;

import com.slack.slack.appConfig.security.form.service.AccountContext;
import com.slack.slack.appConfig.security.form.service.AccountDetailsService;
import com.slack.slack.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
public class FormAuthenticationProvider implements AuthenticationProvider {

    private final AccountDetailsService accountDetailsService;

    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        AccountContext context = (AccountContext) accountDetailsService.loadUserByUsername(authentication.getName());

        String password = (String) authentication.getCredentials();

        if (!passwordEncoder.matches(password, context.getPassword()))
            throw new BadCredentialsException(ErrorCode.INVALID_INPUT_VALUE.getMessage());


        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(context.getUsername(), context.getPassword(), context.getAuthorities());
        token.setDetails(authentication.getDetails());
        return token;
    }

    @Override
    public boolean supports(Class<?> token) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(token);
    }
}
