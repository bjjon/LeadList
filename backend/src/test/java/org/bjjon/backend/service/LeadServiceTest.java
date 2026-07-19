package org.bjjon.backend.service;

import org.bjjon.backend.dto.calllog.CallLogRequest;
import org.bjjon.backend.dto.calllog.CallLogResponse;
import org.bjjon.backend.dto.lead.LeadResponse;
import org.bjjon.backend.entity.CallLog;
import org.bjjon.backend.entity.Lead;
import org.bjjon.backend.entity.Status;
import org.bjjon.backend.entity.User;
import org.bjjon.backend.exception.lead.LeadNotAssignedException;
import org.bjjon.backend.exception.lead.LeadNotFountException;
import org.bjjon.backend.repository.CallLogRepo;
import org.bjjon.backend.repository.LeadRepo;
import org.bjjon.backend.repository.StatusRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LeadServiceTest {

    @Mock
    private LeadRepo leadRepo;

    @Mock
    private CallLogRepo callLogRepo;

    @Mock
    private StatusRepo statusRepo;

    @InjectMocks
    private LeadService leadService;

    private Lead lead1;
    private Lead lead2;
    private User user;

    @BeforeEach
    void setUp() {
        leadService = new LeadService(leadRepo, callLogRepo,  statusRepo);
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
    void getAll_multipleLeads_returnsSortedByLastname() {
        when(leadRepo.findAll()).thenReturn(List.of(lead2, lead1));

        List<LeadResponse> result = leadService.getAll();

        assertEquals(2, result.size());
        assertEquals("Bauer", result.get(0).lastname());
        assertEquals("Fischer", result.get(1).lastname());
    }

    @Test
    void getAll_noLeadsInRepo_returnsEmptyList() {
        when(leadRepo.findAll()).thenReturn(List.of());

        List<LeadResponse> result = leadService.getAll();

        assertTrue(result.isEmpty());
    }

    @Test
    void getAll_mapsEntityFieldsToLeadResponseCorrectly() {
        when(leadRepo.findAll()).thenReturn(List.of(lead1));

        LeadResponse result = leadService.getAll().get(0);

        assertEquals(lead1.getId(), result.id());
        assertEquals(lead1.getFirstname(), result.firstname());
        assertEquals(lead1.getLastname(), result.lastname());
        assertEquals(lead1.getCompany(), result.company());
        assertEquals(lead1.getPhone(), result.phone());
        assertEquals(lead1.getEmail(), result.email());
        assertEquals(lead1.getNote(), result.note());
        assertEquals(lead1.getStatus().getValue(), result.status().value());
        assertEquals(lead1.getCreatedBy().getId(), result.createdBy().id());
    }

    @Test
    void getCallLogs_leadWithCallLogs_returnsMappedCallLogResponses() {
        CallLog callLog1 = CallLog.builder()
                .id(UUID.randomUUID())
                .lead(lead1)
                .user(lead1.getCreatedBy())
                .result(CallLog.CallResult.REACHED)
                .notes("Kunde interessiert, Rückruf vereinbart")
                .calledAt(Instant.now())
                .build();
        CallLog callLog2 = CallLog.builder()
                .id(UUID.randomUUID())
                .lead(lead1)
                .user(lead1.getCreatedBy())
                .result(CallLog.CallResult.NOT_REACHED)
                .notes("Mailbox erreicht")
                .calledAt(Instant.now())
                .build();
        when(callLogRepo.findByLeadId(lead1.getId())).thenReturn(List.of(callLog1, callLog2));

        List<CallLogResponse> result = leadService.getCallLogs(lead1.getId());

        assertEquals(2, result.size());
        assertEquals("REACHED", result.get(0).result());
        assertEquals("NOT_REACHED", result.get(1).result());
    }

    @Test
    void getCallLogs_leadWithoutCallLogs_returnsEmptyList() {
        when(callLogRepo.findByLeadId(lead2.getId())).thenReturn(List.of());

        List<CallLogResponse> result = leadService.getCallLogs(lead2.getId());

        assertTrue(result.isEmpty());
    }

    @Test
    void getCallLogs_callsRepoWithGivenLeadId() {
        when(callLogRepo.findByLeadId(lead1.getId())).thenReturn(List.of());

        leadService.getCallLogs(lead1.getId());

        verify(callLogRepo).findByLeadId(lead1.getId());
    }

    @Test
    void assign_existingLead_setsAssignedToUserAndStatusInProgress() {
        Status inProgressStatus = Status.builder()
                .value("IN_PROGRESS")
                .label("In Bearbeitung")
                .color("#fbbf24")
                .build();

        when(leadRepo.findById(lead1.getId())).thenReturn(Optional.of(lead1));
        when(statusRepo.findStatusByValue("IN_PROGRESS")).thenReturn(inProgressStatus);

        leadService.assign(user, lead1.getId());

        assertEquals(user, lead1.getAssignedTo());
        assertEquals("IN_PROGRESS", lead1.getStatus().getValue());
    }

    @Test
    void assign_existingLead_returnsMappedLeadResponse() {
        Status inProgressStatus = Status.builder()
                .value("IN_PROGRESS")
                .label("In Bearbeitung")
                .color("#fbbf24")
                .build();

        when(leadRepo.findById(lead1.getId())).thenReturn(Optional.of(lead1));
        when(statusRepo.findStatusByValue("IN_PROGRESS")).thenReturn(inProgressStatus);

        LeadResponse result = leadService.assign(user, lead1.getId());

        assertEquals(lead1.getId(), result.id());
        assertEquals(user.getId(), result.assignedTo().id());
        assertEquals("IN_PROGRESS", result.status().value());
    }

    @Test
    void assign_leadNotFound_throwsLeadNotFountException() {
        UUID unknownId = UUID.randomUUID();
        when(leadRepo.findById(unknownId)).thenReturn(Optional.empty());

        assertThrows(LeadNotFountException.class, () -> leadService.assign(user, unknownId));
    }

    @Test
    void unassign_existingLead_clearsAssignedToAndSetsStatusOpen() {
        Status openStatus = Status.builder()
                .value("OPEN")
                .label("Offen")
                .color("#64748b")
                .build();
        lead1.setAssignedTo(user);

        when(leadRepo.findById(lead1.getId())).thenReturn(Optional.of(lead1));
        when(statusRepo.findStatusByValue("OPEN")).thenReturn(openStatus);

        leadService.unassign(lead1.getId());

        assertNull(lead1.getAssignedTo());
        assertEquals("OPEN", lead1.getStatus().getValue());
    }

    @Test
    void unassign_existingLead_returnsMappedLeadResponse() {
        Status openStatus = Status.builder()
                .value("OPEN")
                .label("Offen")
                .color("#64748b")
                .build();
        lead1.setAssignedTo(user);

        when(leadRepo.findById(lead1.getId())).thenReturn(Optional.of(lead1));
        when(statusRepo.findStatusByValue("OPEN")).thenReturn(openStatus);

        LeadResponse result = leadService.unassign(lead1.getId());

        assertEquals(lead1.getId(), result.id());
        assertNull(result.assignedTo());
        assertEquals("OPEN", result.status().value());
    }

    @Test
    void unassign_leadNotFound_throwsLeadNotFountException() {
        UUID unknownId = UUID.randomUUID();
        when(leadRepo.findById(unknownId)).thenReturn(Optional.empty());

        assertThrows(LeadNotFountException.class, () -> leadService.unassign(unknownId));
    }

    @Test
    void logCall_existingLeadAssignedToUser_setsStatusAndSavesCallLog() {
        Status reachedStatus = Status.builder()
                .value("REACHED")
                .label("Erreicht")
                .color("#22c55e")
                .build();
        lead1.setAssignedTo(user);
        CallLogRequest request = new CallLogRequest(CallLog.CallResult.REACHED, "Kunde erreicht, Rückruf vereinbart");

        when(leadRepo.findById(lead1.getId())).thenReturn(Optional.of(lead1));
        when(statusRepo.findStatusByValue("REACHED")).thenReturn(reachedStatus);

        leadService.logCall(user, lead1.getId(), request);

        assertEquals("REACHED", lead1.getStatus().getValue());
        ArgumentCaptor<CallLog> callLogCaptor = ArgumentCaptor.forClass(CallLog.class);
        verify(callLogRepo).save(callLogCaptor.capture());
        CallLog savedCallLog = callLogCaptor.getValue();
        assertEquals(lead1, savedCallLog.getLead());
        assertEquals(user, savedCallLog.getUser());
        assertEquals(CallLog.CallResult.REACHED, savedCallLog.getResult());
        assertEquals("Kunde erreicht, Rückruf vereinbart", savedCallLog.getNotes());
    }

    @Test
    void logCall_existingLeadAssignedToUser_returnsMappedLeadResponse() {
        Status notReachedStatus = Status.builder()
                .value("NOT_REACHED")
                .label("Nicht erreicht")
                .color("#ef4444")
                .build();
        lead1.setAssignedTo(user);
        CallLogRequest request = new CallLogRequest(CallLog.CallResult.NOT_REACHED, "Mailbox erreicht");

        when(leadRepo.findById(lead1.getId())).thenReturn(Optional.of(lead1));
        when(statusRepo.findStatusByValue("NOT_REACHED")).thenReturn(notReachedStatus);

        LeadResponse result = leadService.logCall(user, lead1.getId(), request);

        assertEquals(lead1.getId(), result.id());
        assertEquals("NOT_REACHED", result.status().value());
    }

    @Test
    void logCall_leadNotFound_throwsLeadNotFountException() {
        UUID unknownId = UUID.randomUUID();
        CallLogRequest request = new CallLogRequest(CallLog.CallResult.REACHED, "");
        when(leadRepo.findById(unknownId)).thenReturn(Optional.empty());

        assertThrows(LeadNotFountException.class, () -> leadService.logCall(user, unknownId, request));
    }

    @Test
    void logCall_leadNotAssignedToUser_throwsLeadNotAssignedException() {
        UUID leadId = lead1.getId();
        CallLogRequest request = new CallLogRequest(CallLog.CallResult.REACHED, "");
        when(leadRepo.findById(leadId)).thenReturn(Optional.of(lead1));

        assertThrows(LeadNotAssignedException.class, () -> leadService.logCall(user, leadId, request));
    }
}