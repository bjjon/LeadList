package org.bjjon.backend.dto.chatmessage;

import lombok.Builder;
import lombok.With;
import org.bjjon.backend.dto.user.UserResponse;
import org.bjjon.backend.entity.ChatMessage;

import java.time.Instant;
import java.util.UUID;

@With
@Builder
public record ChatMessageResponse(UUID id, UserResponse sender, String content, Instant createdAt) {

    public static ChatMessageResponse fromEntity(ChatMessage chatMessage) {
        return new ChatMessageResponse(
            chatMessage.getId(),
            UserResponse.fromEntity(chatMessage.getUser()),
            chatMessage.getContent(),
            chatMessage.getCreatedAt()
        );
    }
}
