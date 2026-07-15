package org.bjjon.backend.exception;

import java.time.Instant;

public record ErrorMessage(String message, int status, Instant timestamp) {
}
