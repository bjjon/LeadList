package org.bjjon.backend.service;

import org.bjjon.backend.dto.auth.LoginResponse;
import org.bjjon.backend.entity.User;
import org.bjjon.backend.exception.auth.AuthException;
import org.bjjon.backend.repository.UserRepo;
import org.bjjon.backend.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private static final UUID USER_ID = UUID.fromString("4994802c-b652-4431-8b27-921e76093284");
    private static final String EMAIL = "test@email.com";
    private static final String RAW_PASSWORD = "password123";
    private static final String ENCODED_PASSWORD = "$2a$12$13QTcucQJcSo2DwSntr9OOQYpB8DvHgph//LO.XfunnFZxPLACTzS";
    private static final String TOKEN = "test-jwt-token";

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder().id(USER_ID).email(EMAIL).firstname("Max").lastname("Mustermann").password(ENCODED_PASSWORD).build();
    }

    @Test
    void login_validCredentials_returnsLoginResponseWithToken() {
        when(userRepo.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(RAW_PASSWORD, ENCODED_PASSWORD)).thenReturn(true);
        when(jwtService.generateToken(EMAIL)).thenReturn(TOKEN);

        LoginResponse response = authService.login(EMAIL, RAW_PASSWORD);

        assertNotNull(response);
        assertEquals(TOKEN, response.accessToken());
        assertEquals(USER_ID, response.user().id());
        assertEquals(TOKEN, user.getToken());
        verify(userRepo).save(user);
    }

    @Test
    void login_userNotFound_throwsAuthException() {
        when(userRepo.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThrows(AuthException.class, () -> authService.login(EMAIL, RAW_PASSWORD));
    }

    @Test
    void login_passwordMismatch_throwsAuthException() {
        when(userRepo.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(RAW_PASSWORD, ENCODED_PASSWORD)).thenReturn(false);

        assertThrows(AuthException.class, () -> authService.login(EMAIL, RAW_PASSWORD));
    }

    @Test
    void logout_clearsTokenAndSavesUser() {
        user.setToken(TOKEN);

        authService.logout(user);

        assertNull(user.getToken());
        verify(userRepo).save(user);
    }
}
