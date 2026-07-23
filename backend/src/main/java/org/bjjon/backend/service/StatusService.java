package org.bjjon.backend.service;

import org.bjjon.backend.dto.status.StatusResponse;
import org.bjjon.backend.repository.StatusRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatusService {

    private final StatusRepo statusRepo;

    public StatusService(StatusRepo statusRepo) {
        this.statusRepo = statusRepo;
    }

    public List<StatusResponse> getAll() {
        return statusRepo.findAll().stream().map(StatusResponse::fromEntity).toList();
    }
}
