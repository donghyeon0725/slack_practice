package com.slack.slack.appConfig.security.jwt.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Component
public class JwtTokenResolver {

    private final String type = "bearer";

    // Request의 Header에서 token 값을 가져옵니다. "X-AUTH-TOKEN" : "TOKEN값'
    public String resolveToken(HttpServletRequest request) {

        if (request.getHeader("Authorization") != null) {
            String[] tokens = request.getHeader("Authorization").split(" ");

            if (tokens.length < 1) return null;
            if (!type.toLowerCase().equals(tokens[0].toLowerCase())) return null;

            return tokens[1];
        }

        return request.getHeader("X-AUTH-TOKEN");
    }
}
