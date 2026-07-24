package org.bjjon.backend.exception.lead;

public class DuplicatedLeadException extends RuntimeException {
    public DuplicatedLeadException(String email) {
        super("Duplicated lead email: " + email);
    }
}
