package org.bjjon.backend.service;

import org.bjjon.backend.dto.user.UserResponse;
import org.bjjon.backend.entity.User;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserPresenceService {

    private static final String TOPIC_USERS = "/topic/users";

    private final SimpMessagingTemplate messagingTemplate;

    private final Map<String, User> sessionUsers = new ConcurrentHashMap<>();
    private final Map<UUID, Set<String>> userSessions = new ConcurrentHashMap<>();

    public UserPresenceService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public boolean isOnline(UUID userId) {
        return userSessions.containsKey(userId);
    }

    @EventListener
    public void handleSessionConnected(SessionConnectedEvent event) {
        if (!(event.getUser() instanceof UsernamePasswordAuthenticationToken auth)
                || !(auth.getPrincipal() instanceof User user)) {
            return;
        }

        String sessionId = SimpMessageHeaderAccessor.getSessionId(event.getMessage().getHeaders());
        if (sessionId == null) {
            return;
        }

        sessionUsers.put(sessionId, user);
        Set<String> sessions = userSessions.computeIfAbsent(user.getId(), id -> ConcurrentHashMap.newKeySet());
        boolean wasOffline = sessions.isEmpty();
        sessions.add(sessionId);

        if (wasOffline) {
            messagingTemplate.convertAndSend(TOPIC_USERS, UserResponse.fromEntity(user, true));
        }
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        User user = sessionUsers.remove(event.getSessionId());
        if (user == null) {
            return;
        }

        Set<String> sessions = userSessions.get(user.getId());
        if (sessions == null) {
            return;
        }

        sessions.remove(event.getSessionId());
        if (sessions.isEmpty()) {
            userSessions.remove(user.getId());
            messagingTemplate.convertAndSend(TOPIC_USERS, UserResponse.fromEntity(user, false));
        }
    }
}
