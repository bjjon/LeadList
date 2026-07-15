package org.bjjon.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bjjon.backend.entity.User;
import org.bjjon.backend.exception.auth.AuthException;
import org.bjjon.backend.repository.UserRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    private static final String EMAIL = "test@example.com";

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepo userRepo;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain chain;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_noAuthorizationHeader_proceedsWithoutAuthentication() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilterInternal(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(chain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_nonBearerAuthorizationHeader_proceedsWithoutAuthentication() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Basic someCredentials");

        filter.doFilterInternal(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(chain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_invalidToken_throwsAuthExceptionAndDoesNotProceed() throws Exception {
        String token = "invalid-token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.isValidToken(token)).thenReturn(false);

        assertThrows(AuthException.class, () -> filter.doFilterInternal(request, response, chain));

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(chain, never()).doFilter(any(), any());
    }

    @Test
    void doFilterInternal_userNotFound_throwsAuthExceptionAndDoesNotProceed() throws Exception {
        String token = "valid-token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.isValidToken(token)).thenReturn(true);
        when(jwtService.extractEmail(token)).thenReturn(EMAIL);
        when(userRepo.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThrows(AuthException.class, () -> filter.doFilterInternal(request, response, chain));

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(chain, never()).doFilter(any(), any());
    }

    @Test
    void doFilterInternal_tokenMismatch_throwsAuthExceptionAndDoesNotProceed() throws Exception {
        String token = "valid-token";
        User user = User.builder()
                .email(EMAIL)
                .token("a-different-token")
                .build();
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.isValidToken(token)).thenReturn(true);
        when(jwtService.extractEmail(token)).thenReturn(EMAIL);
        when(userRepo.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        assertThrows(AuthException.class, () -> filter.doFilterInternal(request, response, chain));

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(chain, never()).doFilter(any(), any());
    }

    @Test
    void doFilterInternal_validTokenAndMatchingUser_setsAuthenticationAndProceeds() throws Exception {
        String token = "valid-token";
        User user = User.builder()
                .email(EMAIL)
                .token(token)
                .build();
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.isValidToken(token)).thenReturn(true);
        when(jwtService.extractEmail(token)).thenReturn(EMAIL);
        when(userRepo.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        filter.doFilterInternal(request, response, chain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertInstanceOf(UsernamePasswordAuthenticationToken.class, authentication);
        assertEquals(user, authentication.getPrincipal());
        assertNull(authentication.getCredentials());
        assertTrue(authentication.getAuthorities().isEmpty());
        verify(chain).doFilter(request, response);
    }
}
