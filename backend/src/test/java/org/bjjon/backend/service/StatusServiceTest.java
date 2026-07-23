package org.bjjon.backend.service;

import org.bjjon.backend.dto.status.StatusResponse;
import org.bjjon.backend.entity.Status;
import org.bjjon.backend.repository.StatusRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatusServiceTest {

    @Mock
    private StatusRepo statusRepo;

    @InjectMocks
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
    void getAll_multipleStatus() {
        when(statusRepo.findAll()).thenReturn(List.of(status1, status2));

        List<StatusResponse> result = statusService.getAll();

        assertEquals(2, result.size());
        assertEquals("OPEN", result.get(0).value());
        assertEquals("IN_PROGRESS", result.get(1).value());
    }

    @Test
    void getAll_noStatusInRepo_returnsEmptyList() {
        when(statusRepo.findAll()).thenReturn(List.of());

        List<StatusResponse> result = statusService.getAll();

        assertTrue(result.isEmpty());
    }

    @Test
    void getAll_mapsEntityFieldsToStatusResponseCorrectly() {
        when(statusRepo.findAll()).thenReturn(List.of(status1));

        StatusResponse result = statusService.getAll().getFirst();

        assertEquals(status1.getValue(), result.value());
        assertEquals(status1.getLabel(), result.label());
        assertEquals(status1.getColor(), result.color());
    }
}