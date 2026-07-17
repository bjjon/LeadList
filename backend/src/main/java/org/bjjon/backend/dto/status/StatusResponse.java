package org.bjjon.backend.dto.status;

import org.bjjon.backend.entity.Status;

public record StatusResponse(String value, String label, String color) {

    public static StatusResponse fromEntity(Status status) {
        return new StatusResponse(status.getValue(), status.getLabel(), status.getColor());
    }
}
