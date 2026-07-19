package org.bjjon.backend.exception.lead;

import java.util.UUID;

public class LeadNotAssignedException extends RuntimeException {
    public LeadNotAssignedException(UUID id) {
        super("Lead not assigned to the current user: " + id);
    }
}
