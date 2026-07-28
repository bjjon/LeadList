package org.bjjon.backend.security;

import org.bjjon.backend.entity.User;
import org.bjjon.backend.exception.auth.AuthException;
import org.bjjon.backend.repository.UserRepo;
import org.jspecify.annotations.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final UserRepo userRepo;

    public StompAuthChannelInterceptor(JwtService jwtService, UserRepo userRepo) {
        this.jwtService = jwtService;
        this.userRepo = userRepo;
    }

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String header = accessor.getFirstNativeHeader("Authorization");
            if (header == null || !header.startsWith(BEARER_PREFIX)) {
                throw new AuthException();
            }

            String token = header.substring(BEARER_PREFIX.length());
            if (!jwtService.isValidToken(token)) {
                throw new AuthException();
            }

            String email = jwtService.extractEmail(token);
            User user = userRepo.findByEmail(email)
                    .filter(u -> token.equals(u.getToken()))
                    .orElseThrow(AuthException::new);

            accessor.setUser(new UsernamePasswordAuthenticationToken(user, null, List.of()));
        }

        return message;
    }
}
