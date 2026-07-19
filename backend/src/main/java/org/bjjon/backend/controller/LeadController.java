package org.bjjon.backend.controller;

import jakarta.validation.Valid;
import org.bjjon.backend.dto.calllog.CallLogRequest;
import org.bjjon.backend.dto.calllog.CallLogResponse;
import org.bjjon.backend.dto.lead.LeadResponse;
import org.bjjon.backend.entity.User;
import org.bjjon.backend.service.LeadService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/leads")
public class LeadController {

    private final LeadService leadService;

    public LeadController(LeadService leadService) {
        this.leadService = leadService;
    }

    @GetMapping
    public List<LeadResponse> getAll() {
        return leadService.getAll();
    }

    @GetMapping("/{id}/call-logs")
    public List<CallLogResponse> getCallLogs(@PathVariable UUID id) {
        return leadService.getCallLogs(id);
    }

    @PutMapping("/{id}/assign")
    public LeadResponse assign(@AuthenticationPrincipal User user, @PathVariable UUID id) {
        return leadService.assign(user, id);
    }

    @PutMapping("/{id}/unassign")
    public LeadResponse unassign(@PathVariable UUID id) {
        return leadService.unassign(id);
    }

    @PostMapping("/{id}/call-logs")
    public LeadResponse createCallLog(@AuthenticationPrincipal User user, @PathVariable UUID id, @Valid @RequestBody CallLogRequest callLogRequest) {
        return leadService.logCall(user, id, callLogRequest);
    }
}
