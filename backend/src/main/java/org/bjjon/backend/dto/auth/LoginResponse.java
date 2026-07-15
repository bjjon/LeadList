package org.bjjon.backend.dto.auth;

import lombok.Builder;
import lombok.With;
import org.bjjon.backend.dto.user.UserResponse;

@With
@Builder
public record LoginResponse(String accessToken, UserResponse user) {
}
