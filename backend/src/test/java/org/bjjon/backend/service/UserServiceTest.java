package org.bjjon.backend.service;

import org.bjjon.backend.dto.user.UserResponse;
import org.bjjon.backend.entity.User;
import org.bjjon.backend.repository.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private UserService userService;

    private User user1;
    private static final UUID USER1_UUID = UUID.randomUUID();
    private User user2;
    private static final UUID USER2_UUID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .id(USER1_UUID)
                .email("max@example.com")
                .firstname("Max")
                .lastname("Mustermann")
                .createdAt(Instant.now())
                .build();
        user2 = User.builder()
                .id(USER2_UUID)
                .email("marie@example.com")
                .firstname("Marie")
                .lastname("Musterfrau")
                .createdAt(Instant.now())
                .build();
    }

    @Test
    void getAll_multipleUser() {
        when(userRepo.findAll()).thenReturn(List.of(user1, user2));

        List<UserResponse> result = userService.getAll();

        assertEquals(2, result.size());
        assertEquals(USER1_UUID, result.get(0).id());
        assertEquals(USER2_UUID, result.get(1).id());
    }

    @Test
    void getAll_noUserInRepo_returnsEmptyList() {
        when(userRepo.findAll()).thenReturn(List.of());

        List<UserResponse> result = userService.getAll();

        assertTrue(result.isEmpty());
    }

    @Test
    void getAll_mapsEntityFieldsToUserResponseCorrectly() {
        when(userRepo.findAll()).thenReturn(List.of(user1));

        UserResponse result = userService.getAll().getFirst();

        assertEquals(user1.getId(), result.id());
        assertEquals(user1.getFirstname(), result.firstname());
        assertEquals(user1.getLastname(), result.lastname());
    }
}