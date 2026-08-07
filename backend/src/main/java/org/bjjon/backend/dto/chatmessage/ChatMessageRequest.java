package org.bjjon.backend.dto.chatmessage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatMessageRequest(
        @NotBlank(message = "A message content is required")
        @Size(max = 2000, message = "A message must not exceed 2000 characters")
        String content
) {
}
