package org.bjjon.backend.service;

import org.bjjon.backend.dto.user.UserResponse;
import org.bjjon.backend.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserPresenceServiceTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private UserPresenceService presenceService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder().id(UUID.randomUUID()).firstname("Max").lastname("Mustermann").build();
    }

    private SessionConnectedEvent connectedEvent(String sessionId, User connectedUser) {
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.create();
        accessor.setSessionId(sessionId);
        accessor.setLeaveMutable(true);
        Message<byte[]> message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());
        return new SessionConnectedEvent(this, message, new UsernamePasswordAuthenticationToken(connectedUser, null, List.of()));
    }

    private SessionDisconnectEvent disconnectEvent(String sessionId) {
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.create();
        accessor.setSessionId(sessionId);
        accessor.setLeaveMutable(true);
        Message<byte[]> message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());
        return new SessionDisconnectEvent(this, message, sessionId, CloseStatus.NORMAL);
    }

    @Test
    void handleSessionConnected_firstSessionForUser_broadcastsOnline() {
        presenceService.handleSessionConnected(connectedEvent("session-1", user));

        assertTrue(presenceService.isOnline(user.getId()));
        verify(messagingTemplate).convertAndSend(eq("/topic/users"), eq(UserResponse.fromEntity(user, true)));
    }

    @Test
    void handleSessionConnected_secondSessionForSameUser_doesNotBroadcastAgain() {
        presenceService.handleSessionConnected(connectedEvent("session-1", user));
        presenceService.handleSessionConnected(connectedEvent("session-2", user));

        assertTrue(presenceService.isOnline(user.getId()));
        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/users"), any(UserResponse.class));
    }

    @Test
    void handleSessionDisconnect_oneOfTwoSessions_staysOnlineAndDoesNotBroadcastAgain() {
        presenceService.handleSessionConnected(connectedEvent("session-1", user));
        presenceService.handleSessionConnected(connectedEvent("session-2", user));

        presenceService.handleSessionDisconnect(disconnectEvent("session-1"));

        assertTrue(presenceService.isOnline(user.getId()));
        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/users"), any(UserResponse.class));
    }

    @Test
    void handleSessionDisconnect_lastSession_broadcastsOffline() {
        presenceService.handleSessionConnected(connectedEvent("session-1", user));

        presenceService.handleSessionDisconnect(disconnectEvent("session-1"));

        assertFalse(presenceService.isOnline(user.getId()));
        verify(messagingTemplate).convertAndSend(eq("/topic/users"), eq(UserResponse.fromEntity(user, false)));
    }

    @Test
    void handleSessionDisconnect_unknownSession_doesNothing() {
        presenceService.handleSessionDisconnect(disconnectEvent("unknown-session"));

        verifyNoInteractions(messagingTemplate);
    }
}
