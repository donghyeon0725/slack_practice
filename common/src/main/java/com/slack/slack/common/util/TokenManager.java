package com.slack.slack.common.util;

import com.slack.slack.common.code.ErrorCode;
import com.slack.slack.common.exception.InvalidInputException;
import com.slack.slack.common.code.Key;
import com.slack.slack.common.code.Time;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
        if (!this.isValidateToken(token, key))
            throw new InvalidInputException(ErrorCode.INVALID_INPUT_VALUE);

        return tokenProvider.getMessage(this.resolveToken(token));
    }

    private String resolveToken(String str) {
        String[] tokens = str.split(" ");

        if (tokens.length < 1) return null;
        if (!"bearer".equalsIgnoreCase(tokens[0])) return null;

        return tokens[1];
    }

    /* 유효성 검사 */
    public boolean isValidateToken(String token, Key key) throws InvalidInputException {

        String resolvedToken = this.resolveToken(token);

        /* 토큰이 있는지 & 날짜가 유효한지 & 여기서 발급한 토큰이 맞는지 */
        if (resolvedToken == null || !tokenProvider.validateToken(resolvedToken) || !tokenProvider.isThisToken(key, resolvedToken)) {
            return false;
        }

        return true;
    }

    public String getTokenFromRequest(ServletRequest request) {
        return tokenProvider.resolveToken((HttpServletRequest) request);
    }
}
