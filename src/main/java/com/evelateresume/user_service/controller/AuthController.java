package com.evelateresume.user_service.controller;

import com.evelateresume.user_service.config.TokenCookieConfiguration;
import com.evelateresume.user_service.dto.AuthRequest;
import com.evelateresume.user_service.dto.RegisterRequest;
import com.evelateresume.user_service.service.AuthenticationService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;
    private final TokenCookieConfiguration tokenCookieConfiguration;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid AuthRequest authRequest, HttpServletResponse response) {
        String token = authenticationService.loginUser(authRequest);

        Cookie cookie = new Cookie(tokenCookieConfiguration.getName(), token);
        cookie.setHttpOnly(tokenCookieConfiguration.isHttpOnly());
        cookie.setSecure(tokenCookieConfiguration.isSecure());
        cookie.setPath(tokenCookieConfiguration.getPath());
        cookie.setMaxAge(tokenCookieConfiguration.getMaxAge());
        response.addCookie(cookie);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterRequest registerRequest) {
        authenticationService.registerUser(registerRequest);

        return ResponseEntity.ok("Successfully registered user.");
    }
}
