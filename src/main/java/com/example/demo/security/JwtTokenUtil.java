package com.example.demo.security;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenUtil {

    private final Key key;
    private final long accessTokenExpirationMillis;
    private final long refreshTokenExpirationMillis;
    private final boolean issueRefreshToken;

    public JwtTokenUtil(@Value("${security.jwt.secret:change-me-secret-key-12345-change-me-secret-key-12345}") String secret,
                        @Value("${security.jwt.expiration.access:3600000}") long accessTokenExpirationMillis,
                        @Value("${security.jwt.expiration.refresh:86400000}") long refreshTokenExpirationMillis,
                        @Value("${security.jwt.issue-refresh-token:true}") boolean issueRefreshToken) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessTokenExpirationMillis = accessTokenExpirationMillis;
        this.refreshTokenExpirationMillis = refreshTokenExpirationMillis;
        this.issueRefreshToken = issueRefreshToken;
    }

    public String generateAccessToken(String username, List<String> roles) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + accessTokenExpirationMillis);

        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .claim("token_type", "access")
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(String username, List<String> roles) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + refreshTokenExpirationMillis);

        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .claim("token_type", "refresh")
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public String getUsername(String token) {
        return parseToken(token).getSubject();
    }

    public List<String> getRoles(String token) {
        Claims claims = parseToken(token);
        Object roles = claims.get("roles");
        if (roles instanceof List) {
            return ((List<?>) roles).stream().map(Object::toString).collect(Collectors.toList());
        }
        return List.of();
    }

    public boolean isRefreshTokenEnabled() {
        return issueRefreshToken;
    }

    public boolean isAccessToken(String token) {
        try {
            Claims claims = parseToken(token);
            return "access".equals(claims.get("token_type", String.class));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isRefreshToken(String token) {
        try {
            Claims claims = parseToken(token);
            return "refresh".equals(claims.get("token_type", String.class));
        } catch (Exception e) {
            return false;
        }
    }

    public long getAccessTokenExpirationSeconds() {
        return accessTokenExpirationMillis / 1000;
    }
}
