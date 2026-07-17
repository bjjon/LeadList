package org.bjjon.backend.controller;

import org.bjjon.backend.TestcontainersConfiguration;
import org.bjjon.backend.config.WithMockUserSupportConfig;
import org.bjjon.backend.dto.auth.LoginResponse;
import org.bjjon.backend.dto.user.UserResponse;
import org.bjjon.backend.entity.User;
import org.bjjon.backend.exception.auth.AuthException;
import org.bjjon.backend.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({TestcontainersConfiguration.class, WithMockUserSupportConfig.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    private static final String EMAIL = "test@email.com";
    private static final String RAW_PASSWORD = "password123";

    @Test
    void login_validRequest_returns200WithLoginResponse() throws Exception {
        UserResponse userResponse = new UserResponse(UUID.randomUUID(), "Max", "Mustermann");
        when(authService.login(EMAIL, RAW_PASSWORD))
                .thenReturn(new LoginResponse("test-token", userResponse));

        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content("{\"email\":\"test@email.com\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("test-token"))
                .andExpect(jsonPath("$.user.firstname").value("Max"));
    }

    @Test
    void login_invalidRequestBody_returns400WithValidationErrors() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content("{\"email\":\"not-an-email\",\"password\":\"short\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.password").exists());

        verify(authService, never()).login(any(), any());
    }

    @Test
    void login_invalidCredentials_returns401() throws Exception {
        when(authService.login(anyString(), anyString())).thenThrow(new AuthException());

        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content("{\"email\":\"test@email.com\",\"password\":\"wrongpassword\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @WithMockUser
    void logout_authenticatedUser_returns200() throws Exception {
        User testUser = User.builder()
                .id(UUID.randomUUID())
                .firstname("Max")
                .lastname("Mustermann")
                .email("auth-controller-" + UUID.randomUUID() + "@example.com")
                .password("my-secret-password")
                .build();

        mockMvc.perform(post("/api/auth/logout")
                        .with(authentication(new UsernamePasswordAuthenticationToken(testUser, null, List.of()))))
                .andExpect(status().isOk());

        verify(authService).logout(argThat(user -> user.getId().equals(testUser.getId())));
    }

    @Test
    void logout_unauthenticated_returns401() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isUnauthorized());

        verify(authService, never()).logout(any());
    }
}
