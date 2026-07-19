package org.bjjon.backend.service;

import org.bjjon.backend.dto.calllog.CallLogRequest;
import org.bjjon.backend.dto.calllog.CallLogResponse;
import org.bjjon.backend.dto.lead.LeadResponse;
import org.bjjon.backend.entity.CallLog;
import org.bjjon.backend.entity.Lead;
import org.bjjon.backend.entity.User;
import org.bjjon.backend.exception.lead.LeadNotAssignedException;
import org.bjjon.backend.exception.lead.LeadNotFountException;
import org.bjjon.backend.repository.CallLogRepo;
import org.bjjon.backend.repository.LeadRepo;
import org.bjjon.backend.repository.StatusRepo;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class LeadService {

    private final LeadRepo leadRepo;
    private final CallLogRepo callLogRepo;
    private final StatusRepo statusRepo;

    public LeadService(LeadRepo leadRepo, CallLogRepo callLogRepo, StatusRepo statusRepo) {
        this.leadRepo = leadRepo;
        this.callLogRepo = callLogRepo;
        this.statusRepo = statusRepo;
    }

    public List<LeadResponse> getAll() {
        return leadRepo.findAll().stream()
                .map(LeadResponse::fromEntity)
                .sorted(Comparator.comparing(LeadResponse::lastname))
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

        return LeadResponse.fromEntity(lead);
    }

    public LeadResponse unassign(UUID id) {
        Lead lead = this.leadRepo.findById(id).orElseThrow(() -> new LeadNotFountException(id));

        lead.setAssignedTo(null);
        lead.setStatus(statusRepo.findStatusByValue("OPEN"));
        leadRepo.save(lead);

        return LeadResponse.fromEntity(lead);
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

        lead.setStatus(statusRepo.findStatusByValue(callLogRequest.result().name()));
        leadRepo.save(lead);

        return LeadResponse.fromEntity(lead);
    }
}
