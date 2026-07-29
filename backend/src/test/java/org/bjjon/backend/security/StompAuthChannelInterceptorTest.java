package org.bjjon.backend.security;

import org.bjjon.backend.entity.User;
import org.bjjon.backend.exception.auth.AuthException;
import org.bjjon.backend.repository.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;

import java.security.Principal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StompAuthChannelInterceptorTest {

    private static final String EMAIL = "test@example.com";

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepo userRepo;

    @Mock
    private MessageChannel channel;

    @InjectMocks
    private StompAuthChannelInterceptor interceptor;

    private Message<byte[]> connectMessageWithAuthHeader(String header) {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        if (header != null) {
            accessor.addNativeHeader("Authorization", header);
        }
        accessor.setLeaveMutable(true);
        return MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());
    }

    @Test
    void preSend_connectWithoutAuthorizationHeader_throwsAuthException() {
        Message<byte[]> message = connectMessageWithAuthHeader(null);

        assertThrows(AuthException.class, () -> interceptor.preSend(message, channel));
        verifyNoInteractions(jwtService, userRepo);
    }

    @Test
    void preSend_connectWithNonBearerAuthorizationHeader_throwsAuthException() {
        Message<byte[]> message = connectMessageWithAuthHeader("Basic someCredentials");

        assertThrows(AuthException.class, () -> interceptor.preSend(message, channel));
        verifyNoInteractions(jwtService, userRepo);
    }

    @Test
    void preSend_connectWithInvalidToken_throwsAuthException() {
        String token = "invalid-token";
        Message<byte[]> message = connectMessageWithAuthHeader("Bearer " + token);
        when(jwtService.isValidToken(token)).thenReturn(false);

        assertThrows(AuthException.class, () -> interceptor.preSend(message, channel));
        verifyNoInteractions(userRepo);
    }

    @Test
    void preSend_connectWithUserNotFound_throwsAuthException() {
        String token = "valid-token";
        Message<byte[]> message = connectMessageWithAuthHeader("Bearer " + token);
        when(jwtService.isValidToken(token)).thenReturn(true);
        when(jwtService.extractEmail(token)).thenReturn(EMAIL);
        when(userRepo.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThrows(AuthException.class, () -> interceptor.preSend(message, channel));
    }

    @Test
    void preSend_connectWithTokenMismatch_throwsAuthException() {
        String token = "valid-token";
        User user = User.builder().email(EMAIL).token("a-different-token").build();
        Message<byte[]> message = connectMessageWithAuthHeader("Bearer " + token);
        when(jwtService.isValidToken(token)).thenReturn(true);
        when(jwtService.extractEmail(token)).thenReturn(EMAIL);
        when(userRepo.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        assertThrows(AuthException.class, () -> interceptor.preSend(message, channel));
    }

    @Test
    void preSend_connectWithValidTokenAndMatchingUser_setsUserOnAccessor() {
        String token = "valid-token";
        User user = User.builder().email(EMAIL).token(token).build();
        Message<byte[]> message = connectMessageWithAuthHeader("Bearer " + token);
        when(jwtService.isValidToken(token)).thenReturn(true);
        when(jwtService.extractEmail(token)).thenReturn(EMAIL);
        when(userRepo.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        Message<?> result = interceptor.preSend(message, channel);

        StompHeaderAccessor resultAccessor = MessageHeaderAccessor.getAccessor(result, StompHeaderAccessor.class);
        assertNotNull(resultAccessor);
        Principal principal = resultAccessor.getUser();
        assertNotNull(principal);
        assertInstanceOf(org.springframework.security.authentication.UsernamePasswordAuthenticationToken.class, principal);
        assertEquals(user, ((org.springframework.security.authentication.UsernamePasswordAuthenticationToken) principal).getPrincipal());
    }

    @Test
    void preSend_nonConnectCommand_passesThroughWithoutValidation() {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SEND);
        Message<byte[]> message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

        Message<?> result = interceptor.preSend(message, channel);

        assertSame(message, result);
        verifyNoInteractions(jwtService, userRepo);
    }
}
