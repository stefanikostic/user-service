package com.evelateresume.user_service.security;

import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    @InjectMocks
    private JwtUtil jwtUtil;

    @Mock
    private SecurityJwtConfigurationProperties securityJwtConfigurationProperties;

    @Test
    void generateToken_returnsToken() {
        // given
        when(securityJwtConfigurationProperties.getSecretKey()).thenReturn(Jwts.SIG.HS256.key().build().toString());
        when(securityJwtConfigurationProperties.getExpirationTime()).thenReturn(Long.valueOf(60000));
        Authentication authentication = mock(Authentication.class);

        // when
        String token = jwtUtil.generateToken(authentication, "userId");

        // then
        assertNotNull(token);
    }
}