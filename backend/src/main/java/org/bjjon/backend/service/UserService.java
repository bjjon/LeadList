package org.bjjon.backend.service;

import org.bjjon.backend.dto.user.UserResponse;
import org.bjjon.backend.repository.UserRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepo userRepo;

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public List<UserResponse> getAll() {
        return userRepo.findAll().stream().map(UserResponse::fromEntity).toList();
    }
}
