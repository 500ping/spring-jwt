package com.tomi.jwtsecurity.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.tomi.jwtsecurity.security.SecurityConstants.*;

@Component
public class JwtGenerator {
    public String generateToken(Authentication authentication, String type) {
        String username = authentication.getName();
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + (type == "access" ? JWT_EXPIRATION : JWT_REFRESH_JWT_SECRET));

        Map<String, String> headers = new HashMap<>();
        headers.put("type", type);

        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(currentDate)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET)
                .setHeader(headers)
                .compact();
        return token;
    }

    public String generateTokenFromUsername(String username, String type) {
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + (type == "access" ? JWT_EXPIRATION : JWT_REFRESH_JWT_SECRET));

        Map<String, String> headers = new HashMap<>();
        headers.put("type", type);

        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(currentDate)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET)
                .setHeader(headers)
                .compact();
        return token;
    }

    public String getUsernameFromJwt(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(JWT_SECRET)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public boolean validateToken(String token, String expectType) {
        try {
            String type = (String) Jwts.parser()
                    .setSigningKey(JWT_SECRET)
                    .build()
                    .parseClaimsJws(token)
                    .getHeader()
                    .get("type");
            return type.equals(expectType);
        } catch (Exception ex) {
            throw new AuthenticationCredentialsNotFoundException("JWT was expired or incorrect");
        }
    }
}
