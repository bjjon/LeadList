package org.bjjon.backend.controller;

import org.bjjon.backend.TestcontainersConfiguration;
import org.bjjon.backend.dto.calllog.CallLogResponse;
import org.bjjon.backend.dto.lead.LeadResponse;
import org.bjjon.backend.entity.Lead;
import org.bjjon.backend.entity.Status;
import org.bjjon.backend.entity.User;
import org.bjjon.backend.config.WithMockUserSupportConfig;
import org.bjjon.backend.service.LeadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({TestcontainersConfiguration.class, WithMockUserSupportConfig.class})
class LeadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LeadService leadService;

    private Lead lead1;
    private Lead lead2;
    private User user;

    @BeforeEach
    void setUp() {
        Status status = Status.builder()
                .value("OPEN")
                .label("Offen")
                .color("#64748b")
                .build();

        user = User.builder()
                .id(UUID.randomUUID())
                .firstname("Erika")
                .lastname("Musterfrau")
                .email("erika.musterfrau@example.com")
                .password("irrelevant")
                .build();

        lead1 = Lead.builder()
                .id(UUID.randomUUID())
                .firstname("Anna")
                .lastname("Bauer")
                .company("Acme GmbH")
                .phone("+49 151 23456789")
                .email("anna.bauer@acme.example")
                .note("Interessiert an Premium-Paket")
                .status(status)
                .createdBy(user)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        lead2 = Lead.builder()
                .id(UUID.randomUUID())
                .firstname("Max")
                .lastname("Fischer")
                .company("Beta AG")
                .phone("+49 170 98765432")
                .email("max.fischer@beta.example")
                .note("")
                .status(status)
                .createdBy(user)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Test
    @WithMockUser
    void getAll_authenticatedUser_returns200WithLeadList() throws Exception {
        when(leadService.getAll()).thenReturn(List.of(LeadResponse.fromEntity(lead1), LeadResponse.fromEntity(lead2)));

        mockMvc.perform(get("/api/leads"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].firstname").value("Anna"))
                .andExpect(jsonPath("$[0].lastname").value("Bauer"))
                .andExpect(jsonPath("$[1].firstname").value("Max"))
                .andExpect(jsonPath("$[1].lastname").value("Fischer"));
    }

    @Test
    @WithMockUser
    void getAll_noLeadsExist_returns200WithEmptyList() throws Exception {
        when(leadService.getAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/leads"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getAll_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/api/leads"))
                .andExpect(status().isUnauthorized());

        verify(leadService, never()).getAll();
    }

    @Test
    @WithMockUser
    void getCallLogs_authenticatedUser_returns200WithCallLogList() throws Exception {
        CallLogResponse callLogResponse1 = CallLogResponse.builder()
                .id(UUID.randomUUID())
                .leadId(lead1.getId())
                .userId(UUID.randomUUID())
                .result("REACHED")
                .notes("Kunde interessiert, Rückruf vereinbart")
                .calledAt(Instant.now())
                .build();
        CallLogResponse callLogResponse2 = CallLogResponse.builder()
                .id(UUID.randomUUID())
                .leadId(lead1.getId())
                .userId(UUID.randomUUID())
                .result("NOT_REACHED")
                .notes("Mailbox erreicht")
                .calledAt(Instant.now())
                .build();
        when(leadService.getCallLogs(lead1.getId())).thenReturn(List.of(callLogResponse1, callLogResponse2));

        mockMvc.perform(get("/api/leads/{id}/call-logs", lead1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].result").value("REACHED"))
                .andExpect(jsonPath("$[1].result").value("NOT_REACHED"));
    }

    @Test
    @WithMockUser
    void getCallLogs_leadHasNoCallLogs_returns200WithEmptyList() throws Exception {
        when(leadService.getCallLogs(lead2.getId())).thenReturn(List.of());

        mockMvc.perform(get("/api/leads/{id}/call-logs", lead2.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @WithMockUser
    void getCallLogs_invalidUuidPathVariable_returns400() throws Exception {
        mockMvc.perform(get("/api/leads/{id}/call-logs", "not-a-uuid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCallLogs_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/api/leads/{id}/call-logs", lead1.getId()))
                .andExpect(status().isUnauthorized());

        verify(leadService, never()).getCallLogs(any());
    }

    @Test
    void assign_authenticatedUser_returns200WithLeadResponse() throws Exception {
        lead1.setAssignedTo(user);
        when(leadService.assign(user, lead1.getId())).thenReturn(LeadResponse.fromEntity(lead1));

        mockMvc.perform(put("/api/leads/{id}/assign", lead1.getId())
                        .with(authentication(new UsernamePasswordAuthenticationToken(user, null, List.of()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(lead1.getId().toString()))
                .andExpect(jsonPath("$.assignedTo.id").value(user.getId().toString()));
    }

    @Test
    void assign_invalidUuidPathVariable_returns400() throws Exception {
        mockMvc.perform(put("/api/leads/{id}/assign", "not-a-uuid")
                .with(authentication(new UsernamePasswordAuthenticationToken(user, null, List.of()))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void assign_unauthenticated_returns401() throws Exception {
        mockMvc.perform(put("/api/leads/{id}/assign", lead1.getId()))
                .andExpect(status().isUnauthorized());

        verify(leadService, never()).assign(any(), any());
    }

    @Test
    @WithMockUser
    void unassign_authenticatedUser_returns200WithLeadResponse() throws Exception {
        when(leadService.unassign(lead1.getId())).thenReturn(LeadResponse.fromEntity(lead1));

        mockMvc.perform(put("/api/leads/{id}/unassign", lead1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(lead1.getId().toString()))
                .andExpect(jsonPath("$.assignedTo").value(nullValue()));
    }

    @Test
    @WithMockUser
    void unassign_invalidUuidPathVariable_returns400() throws Exception {
        mockMvc.perform(put("/api/leads/{id}/unassign", "not-a-uuid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void unassign_unauthenticated_returns401() throws Exception {
        mockMvc.perform(put("/api/leads/{id}/unassign", lead1.getId()))
                .andExpect(status().isUnauthorized());

        verify(leadService, never()).unassign(any());
    }
}
