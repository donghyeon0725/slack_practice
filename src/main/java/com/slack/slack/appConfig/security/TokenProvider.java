package com.slack.slack.appConfig.security;

import com.slack.slack.system.Key;
import com.slack.slack.system.Time;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;
import java.util.List;

/**
 * 토큰 생성기
 *
 * @author 김동현
 * @version 1.0, 토큰 생성기 생성
 * @see "src/main/java/com/slack/slack/appConfig/security/JwtTokenProvider.java"
 */
@Component
public class TokenProvider {
    @Value("${system.secretkey}")
    private String secretKey;

    // 객체 초기화, secretKey를 Base64로 인코딩한다.
    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    // JWT 토큰 생성
    public String createToken(Key key, Time time, List<String> messages) {

        /* 정보는 key / value 쌍으로 저장된다. JWT payload 에 저장되는 정보단위 */
        Claims claims = Jwts.claims().setSubject(key.name());

        /* 토큰에 저장할 정보 */
        claims.put(Key.MESSAGE.name(), messages);

        Date now = new Date();

        long tokenValidTime = time.getTime();

        return Jwts.builder()
                /* 정보 저장 */
                .setClaims(claims)
                /* 토큰 발행 시간 정보 */
                .setIssuedAt(now)
                /* set Expire Time */
                .setExpiration(new Date(now.getTime() + tokenValidTime))
                /* 사용할 암호화 알고리즘 & signature 에 들어갈 secret값 세팅*/
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // 토큰에서 필요한 정보를 추출할 수 있도록 함
    public List<String> getMessage(String token) {
        return (List<String>)Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get(Key.MESSAGE.name());
    }

    // 토큰에서 pk 정보 추출
    public String getTokenSubject(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    // 해당 키의 토큰이 맞는지 확인
    public boolean isThisToken(Key key, String token) {
        return this.getTokenSubject(token).equals(key.name());
    }

    // Request의 Header에서 token 값을 가져옵니다. "X-AUTH-TOKEN" : "TOKEN값'
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("X-AUTH-TOKEN");
    }

    // 토큰의 유효성 + 만료일자 확인
    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

}
