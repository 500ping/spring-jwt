package com.tomi.jwtsecurity.security;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;

public class SecurityConstants {
    public static final long JWT_EXPIRATION = 70000;
//    public static final String JWT_SECRET = "secret";
    public static final Key JWT_SECRET = Keys.secretKeyFor(SignatureAlgorithm.HS512);;
}
