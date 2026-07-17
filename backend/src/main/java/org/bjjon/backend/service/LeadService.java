package org.bjjon.backend.service;

import org.bjjon.backend.dto.calllog.CallLogResponse;
import org.bjjon.backend.dto.lead.LeadResponse;
import org.bjjon.backend.repository.CallLogRepo;
import org.bjjon.backend.repository.LeadRepo;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class LeadService {

    private final LeadRepo leadRepo;
    private final CallLogRepo callLogRepo;

    public LeadService(LeadRepo leadRepo, CallLogRepo callLogRepo) {
        this.leadRepo = leadRepo;
        this.callLogRepo = callLogRepo;
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
}
