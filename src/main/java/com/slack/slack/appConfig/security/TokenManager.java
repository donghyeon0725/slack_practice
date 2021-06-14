package com.slack.slack.appConfig.security;

import com.slack.slack.error.exception.ErrorCode;
import com.slack.slack.error.exception.InvalidInputException;
import com.slack.slack.error.exception.InvalidTokenException;
import com.slack.slack.system.Key;
import com.slack.slack.system.Time;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

/**
 * 해더에서 토큰을 추출 & 토큰 정보 추출 & 토큰 유효성 검사기
 *
 * @author 김동현
 * @version 1.0, 토큰 관리자 생성
 * @see "src/main/java/com/slack/slack/appConfig/filter/JwtAuthenticationFilter.java"
 */
@Component
@AllArgsConstructor
public class TokenManager {

    private TokenProvider tokenProvider;

    /* 토큰 생성 */
    public String createToken(Key key, Time time, List<String> messages) {
        /* 시간 유효성 검사 진행 */
        return tokenProvider.createToken(key, time, messages);
    }

    /* 토큰에 담긴 정보 가져오기 */
    public List<String> get(String token, Key key) {
        /* 유효하지 않으면 */
        if (!isInvalid(token, key)) {
            throw new InvalidTokenException(ErrorCode.INVALID_INPUT_VALUE);
        }

        return tokenProvider.getMessage(token);
    }

    /* 유효성 검사 */
    public boolean isInvalid(String token, Key key) throws InvalidInputException {
        /* 토큰이 있는지 & 날짜가 유효한지 & 여기서 발급한 토큰이 맞는지 */
        if (token == null || !tokenProvider.validateToken(token) || !tokenProvider.isThisToken(key, token)) {
            throw new InvalidInputException(ErrorCode.INVALID_INPUT_VALUE);
        }
        return true;
    }

    public String getTokenFromRequest(ServletRequest request) {
        return tokenProvider.resolveToken((HttpServletRequest) request);
    }
}
