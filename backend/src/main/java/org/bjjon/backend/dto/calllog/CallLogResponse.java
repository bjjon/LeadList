package org.bjjon.backend.dto.calllog;

import lombok.Builder;
import lombok.With;
import org.bjjon.backend.entity.CallLog;

import java.time.Instant;
import java.util.UUID;

@With
@Builder
public record CallLogResponse(UUID id, UUID leadId, UUID userId, String result, String notes, Instant calledAt) {

    public static CallLogResponse fromEntity(CallLog callLog) {
        return new CallLogResponse(
            callLog.getId(),
            callLog.getLead().getId(),
            callLog.getUser().getId(),
            callLog.getResult().name(),
            callLog.getNotes(),
            callLog.getCalledAt()
        );
    }
}