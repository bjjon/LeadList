package org.bjjon.backend.controller;

import org.bjjon.backend.TestcontainersConfiguration;
import org.bjjon.backend.config.WithMockUserSupportConfig;
import org.bjjon.backend.dto.user.UserResponse;
import org.bjjon.backend.entity.User;
import org.bjjon.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({TestcontainersConfiguration.class, WithMockUserSupportConfig.class})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
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
    @WithMockUser
    void getAll_authenticatedUser_returns200WithUserList() throws Exception {
        when(userService.getAll()).thenReturn(List.of(UserResponse.fromEntity(user1), UserResponse.fromEntity(user2)));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(USER1_UUID.toString()))
                .andExpect(jsonPath("$[0].firstname").value("Max"))
                .andExpect(jsonPath("$[0].lastname").value("Mustermann"))
                .andExpect(jsonPath("$[1].id").value(USER2_UUID.toString()))
                .andExpect(jsonPath("$[1].firstname").value("Marie"))
                .andExpect(jsonPath("$[1].lastname").value("Musterfrau"));
    }

    @Test
    @WithMockUser
    void getAll_noUserExist_returns200WithEmptyList() throws Exception {
        when(userService.getAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getAll_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());

        verify(userService, never()).getAll();
    }
}