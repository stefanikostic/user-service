package com.evelateresume.user_service.service;

import com.evelateresume.user_service.entity.User;
import com.evelateresume.user_service.entity.UserRole;
import com.evelateresume.user_service.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Test
    void loadUserByUsername_userNotFound_throwsException() {
        // given
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        // when
        // then
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("username"));
    }

    @Test
    void loadUserByUsername_userExists_returnsUserDetails() {
        // given
        User user = UserTestData.getUser();
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        // when
        UserDetails result = userService.loadUserByUsername("username");

        // then
        assertNotNull(result);
        assertEquals("username", result.getUsername());
        assertEquals("password", result.getPassword());
        assertNotNull(result.getAuthorities());
        GrantedAuthority authority = result.getAuthorities().stream().findFirst().orElse(null);
        assertNotNull(authority);
        assertEquals(UserRole.ADMIN.name(), authority.getAuthority());
    }
}