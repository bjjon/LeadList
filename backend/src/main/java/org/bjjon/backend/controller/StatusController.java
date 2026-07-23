package org.bjjon.backend.controller;

import org.bjjon.backend.dto.status.StatusResponse;
import org.bjjon.backend.service.StatusService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/status")
public class StatusController {

    private final StatusService statusService;

    public StatusController(StatusService statusService) {
        this.statusService = statusService;
    }

    @GetMapping
    public List<StatusResponse> getAll() {
        return statusService.getAll();
    }
}
