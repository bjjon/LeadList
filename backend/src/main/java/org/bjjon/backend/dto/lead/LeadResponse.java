package org.bjjon.backend.dto.lead;

import org.bjjon.backend.dto.status.StatusResponse;
import org.bjjon.backend.dto.user.UserResponse;
import org.bjjon.backend.entity.Lead;

import java.time.Instant;
import java.util.UUID;

public record LeadResponse(
    UUID id,
    String firstname,
    String lastname,
    String company,
    String phone,
    String email,
    String note,
    StatusResponse status,
    UserResponse createdBy,
    UserResponse assignedTo,
    Instant createdAt
) {

    public static LeadResponse fromEntity(Lead lead) {
        return new LeadResponse(
            lead.getId(),
            lead.getFirstname(),
            lead.getLastname(),
            lead.getCompany(),
            lead.getPhone(),
            lead.getEmail(),
            lead.getNote(),
            StatusResponse.fromEntity(lead.getStatus()),
            lead.getCreatedBy() != null ? UserResponse.fromEntity(lead.getCreatedBy()) : null,
            lead.getAssignedTo() != null ? UserResponse.fromEntity(lead.getAssignedTo()) : null,
            lead.getCreatedAt()
        );
    }
}
