package org.bjjon.backend.service;

import org.bjjon.backend.dto.user.UserResponse;
import org.bjjon.backend.repository.UserRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepo userRepo;
    private final UserPresenceService userPresenceService;

    public UserService(UserRepo userRepo, UserPresenceService userPresenceService) {
        this.userRepo = userRepo;
        this.userPresenceService = userPresenceService;
    }

    public List<UserResponse> getAll() {
        return userRepo.findAll().stream()
                .map(user -> UserResponse.fromEntity(user, userPresenceService.isOnline(user.getId())))
                .toList();
    }
}
