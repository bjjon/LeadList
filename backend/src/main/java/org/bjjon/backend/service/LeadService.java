package org.bjjon.backend.service;

import org.bjjon.backend.dto.calllog.CallLogRequest;
import org.bjjon.backend.dto.calllog.CallLogResponse;
import org.bjjon.backend.dto.lead.LeadRequest;
import org.bjjon.backend.dto.lead.LeadResponse;
import org.bjjon.backend.entity.CallLog;
import org.bjjon.backend.entity.Lead;
import org.bjjon.backend.entity.User;
import org.bjjon.backend.exception.lead.DuplicatedLeadException;
import org.bjjon.backend.exception.lead.LeadNotAssignedException;
import org.bjjon.backend.exception.lead.LeadNotFountException;
import org.bjjon.backend.repository.CallLogRepo;
import org.bjjon.backend.repository.LeadRepo;
import org.bjjon.backend.repository.StatusRepo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class LeadService {

    private static final String TOPIC_LEADS = "/topic/leads";
    private static final String TOPIC_CALL_LOGS = "/topic/call-logs";

    private final LeadRepo leadRepo;
    private final CallLogRepo callLogRepo;
    private final StatusRepo statusRepo;
    private final SimpMessagingTemplate messagingTemplate;

    public LeadService(LeadRepo leadRepo, CallLogRepo callLogRepo, StatusRepo statusRepo, SimpMessagingTemplate messagingTemplate) {
        this.leadRepo = leadRepo;
        this.callLogRepo = callLogRepo;
        this.statusRepo = statusRepo;
        this.messagingTemplate = messagingTemplate;
    }

    public List<LeadResponse> getAll() {
        return leadRepo.findAll().stream()
                .map(LeadResponse::fromEntity)
                .sorted(Comparator.comparing(LeadResponse::createdAt).reversed())
                .toList();
    }

    public List<CallLogResponse> getCallLogs(UUID leadId) {
        return callLogRepo.findByLeadId(leadId).stream()
                .map(CallLogResponse::fromEntity)
                .toList();
    }

    public LeadResponse assign(User user, UUID id) {
        Lead lead = this.leadRepo.findById(id).orElseThrow(() -> new LeadNotFountException(id));

        lead.setAssignedTo(user);
        lead.setStatus(statusRepo.findStatusByValue("IN_PROGRESS"));
        leadRepo.save(lead);

        LeadResponse response = LeadResponse.fromEntity(lead);
        messagingTemplate.convertAndSend(TOPIC_LEADS, response);
        return response;
    }

    public LeadResponse unassign(UUID id) {
        Lead lead = this.leadRepo.findById(id).orElseThrow(() -> new LeadNotFountException(id));

        lead.setAssignedTo(null);
        lead.setStatus(statusRepo.findStatusByValue("OPEN"));
        leadRepo.save(lead);

        LeadResponse response = LeadResponse.fromEntity(lead);
        messagingTemplate.convertAndSend(TOPIC_LEADS, response);
        return response;
    }

    public LeadResponse logCall(User user, UUID id, CallLogRequest callLogRequest) {
        Lead lead = this.leadRepo.findById(id).orElseThrow(() -> new LeadNotFountException(id));

        if (lead.getAssignedTo() == null || !lead.getAssignedTo().getId().equals(user.getId())) {
            throw new LeadNotAssignedException(id);
        }

        CallLog callLog = CallLog.builder()
                .lead(lead)
                .user(user)
                .result(callLogRequest.result())
                .notes(callLogRequest.notes())
                .build();
        callLogRepo.save(callLog);
        messagingTemplate.convertAndSend(TOPIC_CALL_LOGS, CallLogResponse.fromEntity(callLog));

        lead.setStatus(statusRepo.findStatusByValue(callLogRequest.result().name()));
        leadRepo.save(lead);

        LeadResponse response = LeadResponse.fromEntity(lead);
        messagingTemplate.convertAndSend(TOPIC_LEADS, response);

        return response;
    }

    public LeadResponse addLead(User user, LeadRequest leadRequest) {

        if (this.leadRepo.existsByEmail(leadRequest.email())) {
            throw new DuplicatedLeadException(leadRequest.email());
        }

        Lead newLead = new Lead();

        newLead.setFirstname(leadRequest.firstname());
        newLead.setLastname(leadRequest.lastname());
        newLead.setCompany(leadRequest.company());
        newLead.setEmail(leadRequest.email());
        newLead.setCreatedBy(user);
        newLead.setStatus(statusRepo.findStatusByValue("OPEN"));
        newLead.setPhone(leadRequest.phone());
        newLead.setNote(leadRequest.note());

        leadRepo.save(newLead);

        LeadResponse response = LeadResponse.fromEntity(newLead);
        messagingTemplate.convertAndSend(TOPIC_LEADS, response);

        return response;
    }
}
