package com.slack.slack.security.jwt.token;

import com.slack.slack.common.code.Key;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;
import java.util.stream.Collectors;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {
    private Object principal;
    private Object credentials;
    private String token;
    private String secretKey;


    /**
     * 인증 전에는 토큰 정보만 가지고 있는다.
     * */
    public JwtAuthenticationToken(String token, String secretKey) {
        super((Collection)null);
        this.token = token;
        this.secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        if (this.isValidateToken())
            this.principal = getUsername();
        this.setAuthenticated(false);
    }

    /**
     * 인증 후 생성
     * */
    public JwtAuthenticationToken(String token, String secretKey, Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        this.token = token;
        this.secretKey = secretKey;
        super.setAuthenticated(true);
    }

    // 토큰의 유효성 + 만료일자 확인
    public boolean isValidateToken() {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(this.secretKey).parseClaimsJws(this.token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    // 토큰에서 회원 정보 추출
    private String getUsername() {
        return Jwts.parser().setSigningKey(this.secretKey).parseClaimsJws(this.token).getBody().getSubject();
    }

    // 권한 목록 추출
    @Override
    public Collection<GrantedAuthority> getAuthorities () {
        List<HashMap> list = (List<HashMap>)Jwts.parser().setSigningKey(this.secretKey).parseClaimsJws(this.token).getBody().get(Key.ROLES.getKey());

        if (list == null)
            return null;

        return list.stream().map(s -> new SimpleGrantedAuthority(s.get("authority").toString())).collect(Collectors.toList());
    }

    public String getToken() {
        return token;
    }

    public String getSecretKey() {
        return secretKey;
    }

    /**
     * 인증이 완료된 토큰인지 확인하기 위함
     * */
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if (isAuthenticated) {
            throw new IllegalArgumentException("Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        } else {
            super.setAuthenticated(false);
        }
    }

    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }


}
