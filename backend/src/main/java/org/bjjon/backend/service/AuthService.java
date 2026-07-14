package org.bjjon.backend.service;

import org.bjjon.backend.dto.auth.LoginResponse;
import org.bjjon.backend.dto.user.UserResponse;
import org.bjjon.backend.entity.User;
import org.bjjon.backend.exception.auth.AuthException;
import org.bjjon.backend.repository.UserRepo;
import org.bjjon.backend.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;


    public AuthService(UserRepo userRepo, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(String email, String password) {
        User user = userRepo.findByEmail(email).orElseThrow(AuthException::new);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new AuthException();
        }

        String token = jwtService.generateToken(user.getEmail());
        user.setToken(token);
        userRepo.save(user);

        UserResponse userResponse = UserResponse.fromEntity(user);
        return new LoginResponse(token, userResponse);
    }

    public void logout(User user) {
        user.setToken(null);
        userRepo.save(user);
    }
}
