package com.evelateresume.user_service.service;

import com.evelateresume.user_service.dto.AuthRequest;
import com.evelateresume.user_service.dto.RegisterRequest;
import com.evelateresume.user_service.entity.User;
import com.evelateresume.user_service.entity.UserRole;
import com.evelateresume.user_service.exception.UnauthorizedException;
import com.evelateresume.user_service.exception.UserAlreadyExistsException;
import com.evelateresume.user_service.exception.UsernameAlreadyExistsException;
import com.evelateresume.user_service.exception.UsernameNotFoundException;
import com.evelateresume.user_service.repository.UserRepository;
import com.evelateresume.user_service.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
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
        Optional<User> user = userRepository.findByUsername(authRequest.username());
        if (user.isEmpty()) {
            throw new UsernameNotFoundException();
        }
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.username(), authRequest.password())
        );
        String userId = user.get().getId();

        String token = jwtUtil.generateToken(authentication, userId);
        if (token == null) {
            throw new UnauthorizedException();
        }
        return token;
    }

    public UserDetails registerUser(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.email())) {
            throw new UserAlreadyExistsException();
        }
        if (userRepository.existsByUsername(registerRequest.username())) {
            throw new UsernameAlreadyExistsException();
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
