package com.slack.slack.appConfig.security.jwt.provider;

import com.slack.slack.appConfig.security.form.service.AccountContext;
import com.slack.slack.appConfig.security.form.service.AccountDetailsService;
import com.slack.slack.appConfig.security.jwt.domain.UserContext;
import com.slack.slack.appConfig.security.jwt.token.JwtAuthenticationToken;
import com.slack.slack.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;

    // 여기서 받은 authentication 객체는 토큰을 포함하고 있습니다.
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken)authentication;

        // 토큰의 유효성 검사
        if (!authenticationToken.isValidateToken())
            throw new BadCredentialsException(ErrorCode.INVALID_INPUT_VALUE.getMessage());

        // 유효성 검사를 통과한 경우 DB 조회 후 Authentication 객체 생성
        UserContext context = (UserContext) userDetailsService.loadUserByUsername(authenticationToken.getName());

        JwtAuthenticationToken token =
                new JwtAuthenticationToken(
                    authenticationToken.getToken(),
                    authenticationToken.getSecretKey(),
                    context.getUsername(),
                    context.getPassword(),
                    context.getAuthorities());
        token.setDetails(authentication.getDetails());
        return token;
    }

    // 커스텀한 토큰의 인증을 지원합니다.
    @Override
    public boolean supports(Class<?> token) {
        return JwtAuthenticationToken.class.isAssignableFrom(token);
    }
}
