package com.evelateresume.user_service.service;

import com.evelateresume.user_service.dto.AuthRequest;
import com.evelateresume.user_service.dto.RegisterRequest;
import com.evelateresume.user_service.entity.User;
import com.evelateresume.user_service.entity.UserRole;
import com.evelateresume.user_service.repository.UserRepository;
import com.evelateresume.user_service.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public String loginUser(AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.username(), authRequest.password())
        );
        Optional<User> user = userRepository.findByUsername(authentication.getName());
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User doesn't exist");
        }
        String userId = user.get().getId();
        return jwtUtil.generateToken(authentication, userId);
    }

    public UserDetails registerUser(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.username())) {
            throw new RuntimeException("User already exists!");
        }

        String encodedPassword = bCryptPasswordEncoder.encode(registerRequest.password());
        User user = new User();
        user.setUsername(registerRequest.username());
        user.setPassword(encodedPassword);
        user.setFullName(registerRequest.fullName());
        user.setRole(UserRole.USER.name());
        user.setEmail(registerRequest.email());

        return userRepository.save(user);
    }
}
