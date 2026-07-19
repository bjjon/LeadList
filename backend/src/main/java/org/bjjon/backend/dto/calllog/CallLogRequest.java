package org.bjjon.backend.dto.calllog;

import jakarta.validation.constraints.NotNull;
import org.bjjon.backend.entity.CallLog;

public record CallLogRequest(
        @NotNull(message = "A result is required")
        CallLog.CallResult result,
        String notes
) {
}
