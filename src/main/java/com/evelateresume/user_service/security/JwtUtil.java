package com.evelateresume.user_service.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecureDigestAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final SecureDigestAlgorithm<SecretKey, ?> algorithm = Jwts.SIG.HS256;
    private final SecurityJwtConfigurationProperties securityJwtConfigurationProperties;

    public String generateToken(Authentication authentication, String userId) {
        String secretKey = securityJwtConfigurationProperties.getSecretKey();
        long expiration = securityJwtConfigurationProperties.getExpirationTime();

        return Jwts.builder()
                .subject(authentication.getName())
                .claim("userId", userId)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .compact();
    }
}
