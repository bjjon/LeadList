package org.bjjon.backend.controller;

import org.bjjon.backend.TestcontainersConfiguration;
import org.bjjon.backend.config.WithMockUserSupportConfig;
import org.bjjon.backend.dto.status.StatusResponse;
import org.bjjon.backend.entity.Status;
import org.bjjon.backend.service.StatusService;
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

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({TestcontainersConfiguration.class, WithMockUserSupportConfig.class})
class StatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StatusService statusService;

    private Status status1;
    private Status status2;

    @BeforeEach
    void setUp() {
        status1 = Status.builder()
                .value("OPEN")
                .label("Offen")
                .color("#64748b")
                .build();
        status2 = Status.builder()
                .value("IN_PROGRESS")
                .label("In Bearbeitung")
                .color("#fbbf24")
                .build();
    }

    @Test
    @WithMockUser
    void getAll_authenticatedUser_returns200WithStatusList() throws Exception {
        when(statusService.getAll()).thenReturn(List.of(StatusResponse.fromEntity(status1), StatusResponse.fromEntity(status2)));

        mockMvc.perform(get("/api/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].value").value("OPEN"))
                .andExpect(jsonPath("$[0].label").value("Offen"))
                .andExpect(jsonPath("$[0].color").value("#64748b"))
                .andExpect(jsonPath("$[1].value").value("IN_PROGRESS"))
                .andExpect(jsonPath("$[1].label").value("In Bearbeitung"))
                .andExpect(jsonPath("$[1].color").value("#fbbf24"));
    }

    @Test
    @WithMockUser
    void getAll_noStatusExist_returns200WithEmptyList() throws Exception {
        when(statusService.getAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getAll_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/api/status"))
                .andExpect(status().isUnauthorized());

        verify(statusService, never()).getAll();
    }
}