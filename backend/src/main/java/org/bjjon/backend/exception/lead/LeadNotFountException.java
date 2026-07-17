package org.bjjon.backend.exception.lead;

import java.util.UUID;

public class LeadNotFountException extends RuntimeException {
    public LeadNotFountException(UUID id) {
        super("Could not find lead with id: " + id);
    }
}
