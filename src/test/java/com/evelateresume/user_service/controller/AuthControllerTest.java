package com.evelateresume.user_service.controller;

import com.evelateresume.user_service.config.TokenCookieConfiguration;
import com.evelateresume.user_service.security.SecurityConfig;
import com.evelateresume.user_service.service.AuthenticationService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@Import(SecurityConfig.class)
@AutoConfigureMockMvc
class AuthControllerTest {

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private TokenCookieConfiguration tokenCookieConfiguration;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @SneakyThrows
    void login_usernameIsNull_returnsBadRequest() {
        // given
        String authRequest = """
            {
                "password": "password1"
            }
            """;

        // when
        // then
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(authRequest))
                .andExpect(status().isBadRequest());
    }

    @ValueSource(strings = {"ab", "abcdfgehjklmnopqrstuvwxyz", "-ab", "ab1@"})
    @ParameterizedTest
    @SneakyThrows
    void login_usernameDoesNotFollowRegex_returnsBadRequest(String username) {
        // given
        String authRequest = String.format("""
            {
                "username": "%s",
                "password": "password1"
            }
            """, username);

        // when
        // then
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void login_passwordIsNull_returnsBadRequest() {
        // given
        String authRequest = """
            {
                "username": "username"
            }
            """;

        // when
        // then
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authRequest))
                .andExpect(status().isBadRequest());
    }

    @ValueSource(strings = {"ab", "abcdfgehjklmnopqrstuvwxyz", "-ab", "ab1@"})
    @ParameterizedTest
    @SneakyThrows
    void register_usernameDoesNotFollowRegex_returnsBadRequest(String username) {
        // given
        String authRequest = String.format("""
            {
                "username": "%s",
                "password": "password1",
                "fullName": "Stefani Kostic",
                "email": "stefanikostic6@gmail.com"
            }
            """, username);

        // when
        // then
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authRequest))
                .andExpect(status().isBadRequest());
    }

    @ValueSource(strings = {"abcdefgh", "abcdfgehjklmnopqrstuvwxyz", "Ab1@", "-ab", "ab1@"})
    @ParameterizedTest
    @SneakyThrows
    void register_passwordDoesNotFollowRegex_returnsBadRequest(String password) {
        // given
        String authRequest = String.format("""
            {
                "username": "username",
                "password": "%s",
                "fullName": "Stefani Kostic",
                "email": "stefanikostic6@gmail.com"
            }
            """, password);

        // when
        // then
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authRequest))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @ValueSource(strings = {"Ab1@", "-ab", "ab1@"})
    @ParameterizedTest
    @SneakyThrows
    void register_fullNameDoesNotFollowRegex_returnsBadRequest(String fullName) {
        // given
        String authRequest = String.format("""
            {
                "username": "username",
                "password": "Password1*",
                "fullName": "%s",
                "email": "stefanikostic6@gmail.com"
            }
            """, fullName);

        // when
        // then
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authRequest))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @ValueSource(strings = {"absjdidjjdid", "Ab1@", "-ab", "ab1@"})
    @ParameterizedTest
    @SneakyThrows
    void register_emailDoesNotFollowRegex_returnsBadRequest(String email) {
        // given
        String authRequest = String.format("""
            {
                "username": "username",
                "password": "Password1*",
                "fullName": "Stefani Kostic",
                "email": "%s"
            }
            """, email);

        // when
        // then
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authRequest))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    @SneakyThrows
    void login_loginUserIsSuccessful_returnsOk() {
        // given
        String authRequest = """
            {
                "username": "username",
                "password": "password"
            }
            """;
        when(authenticationService.loginUser(any())).thenReturn("token");
        when(tokenCookieConfiguration.getName()).thenReturn("AUTH_TOKEN");
        when(tokenCookieConfiguration.getPath()).thenReturn("/");
        when(tokenCookieConfiguration.getMaxAge()).thenReturn(3600);
        when(tokenCookieConfiguration.isHttpOnly()).thenReturn(true);
        when(tokenCookieConfiguration.isSecure()).thenReturn(false);

        // when
        // then
        var result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authRequest))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo("token");
    }

    @Test
    @SneakyThrows
    void register_registerUserIsSuccessful_returnsOk() {
        // given
        String authRequest = """
            {
                "username": "username",
                "password": "Password1*",
                "fullName": "Stefani Kostic",
                "email": "stefanikostic6@gmial.com"
            }
            """;

        // when
        // then
        var result = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authRequest))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo("Successfully registered user.");
    }
}