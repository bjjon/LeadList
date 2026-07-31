package org.bjjon.backend.dto.user;

import org.bjjon.backend.entity.User;

import java.util.UUID;

public record UserResponse(UUID id, String firstname, String lastname, boolean online) {

    public static UserResponse fromEntity(User user, boolean online) {
        return new UserResponse(user.getId(), user.getFirstname(), user.getLastname(), online);
    }

    public static UserResponse fromEntity(User user) {
        return fromEntity(user, false);
    }
}