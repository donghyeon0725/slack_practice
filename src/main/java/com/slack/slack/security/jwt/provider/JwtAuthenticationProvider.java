package com.slack.slack.security.jwt.provider;

import com.slack.slack.security.jwt.token.JwtAuthenticationToken;
import com.slack.slack.common.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

    // 여기서 받은 authentication 객체는 토큰을 포함하고 있습니다.
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken)authentication;

        // 토큰의 유효성 검사
        if (!authenticationToken.isValidateToken())
            throw new BadCredentialsException(ErrorCode.INVALID_INPUT_VALUE.getMessage());

        // 유효성 검사를 통과한 경우 DB 조회 후 Authentication 객체 생성
//        UserContext context = (UserContext) userDetailsService.loadUserByUsername(authenticationToken.getName());

        JwtAuthenticationToken token =
                new JwtAuthenticationToken(
                    authenticationToken.getToken(),
                    authenticationToken.getSecretKey(),
                    authenticationToken.getPrincipal(),
                    null,
                    authentication.getAuthorities());
        token.setDetails(authentication.getDetails());

        if (token != null) {

            log.debug("authentication complete authorities : ");

            for (GrantedAuthority auth : authentication.getAuthorities()) {
                log.debug(auth.getAuthority());
            }

        }

        return token;
    }

    // 커스텀한 토큰의 인증을 지원합니다.
    @Override
    public boolean supports(Class<?> token) {
        return JwtAuthenticationToken.class.isAssignableFrom(token);
    }
}
