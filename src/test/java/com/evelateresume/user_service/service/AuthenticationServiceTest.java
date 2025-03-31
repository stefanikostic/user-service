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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @InjectMocks
    private AuthenticationService authenticationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Test
    void loginUser_userIsNotFound_throwsException() {
        // given
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        AuthRequest authRequest = new AuthRequest("stefanikostic", "password");

        // when
        // then
        assertThatThrownBy(() -> authenticationService.loginUser(authRequest))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void loginUser_tokenIsNull_throwsException() {
        // given
        User user = UserTestData.getUser();
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.ofNullable(user));
        AuthRequest authRequest = new AuthRequest("stefanikostic", "password");

        // when
        // then
        assertThatThrownBy(() -> authenticationService.loginUser(authRequest))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void loginUser_loginIsSuccessful_returnsToken() {
        // given
        User user = UserTestData.getUser();
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.ofNullable(user));
        when(jwtUtil.generateToken(any(), anyString())).thenReturn("token");
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        AuthRequest authRequest = new AuthRequest("stefanikostic", "password");

        // when
        String token = authenticationService.loginUser(authRequest);

        // then
        assertThat(token).isNotNull();
    }

    @Test
    void registerUser_userEmailExists_throwsException() {
        // given
        when(userRepository.existsByEmail(anyString())).thenReturn(true);
        RegisterRequest registerRequest = new RegisterRequest("stefanikostic", "password", "Stefani Kostic", "stefanikostic@hotmail.com");

        // when
        // then
        assertThatThrownBy(() -> authenticationService.registerUser(registerRequest))
                .isInstanceOf(UserAlreadyExistsException.class);
    }

    @Test
    void registerUser_usernameExists_throwsException() {
        // given
        when(userRepository.existsByUsername(anyString())).thenReturn(true);
        RegisterRequest registerRequest = new RegisterRequest("stefanikostic", "password", "Stefani Kostic", "stefanikostic@hotmail.com");

        // when
        // then
        assertThatThrownBy(() -> authenticationService.registerUser(registerRequest))
                .isInstanceOf(UsernameAlreadyExistsException.class);
    }

    @Test
    void registerUser_registerIsSuccessful_verifyUserDetails() {
        // given
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn("passwordEncoded");
        RegisterRequest registerRequest = new RegisterRequest("stefanikostic", "password", "Stefani Kostic", "stefanikostic@hotmail.com");

        // when
        authenticationService.registerUser(registerRequest);

        // then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());
        User capturedUser = userArgumentCaptor.getValue();
        assertThat(capturedUser).isNotNull();
        assertThat(capturedUser.getUsername()).isEqualTo("stefanikostic");
        assertThat(capturedUser.getEmail()).isEqualTo("stefanikostic@hotmail.com");
        assertThat(capturedUser.getFullName()).isEqualTo("Stefani Kostic");
        assertThat(capturedUser.getPassword()).isEqualTo("passwordEncoded");
        assertThat(capturedUser.getRole()).isEqualTo(UserRole.USER.name());
    }
}