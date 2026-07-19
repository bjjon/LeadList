package org.bjjon.backend.exception;

import org.bjjon.backend.exception.auth.AuthException;
import org.bjjon.backend.exception.lead.LeadNotAssignedException;
import org.bjjon.backend.exception.lead.LeadNotFountException;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    protected @Nullable ResponseEntity<@NonNull Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, @NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request) {
        Map<String, String> validationErrors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(fieldError -> validationErrors.put(fieldError.getField(), fieldError.getDefaultMessage()));

        return new ResponseEntity<>(validationErrors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorMessage handleAuthException(AuthException ex) {
        return new ErrorMessage(ex.getMessage(), HttpStatus.UNAUTHORIZED.value(), Instant.now());
    }

    @ExceptionHandler(LeadNotFountException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage handleLeadNotFountException(LeadNotFountException ex) {
        return new ErrorMessage(ex.getMessage(), HttpStatus.NOT_FOUND.value(), Instant.now());
    }

    @ExceptionHandler(LeadNotAssignedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorMessage handleLeadNotAssignedException(LeadNotAssignedException ex) {
        return new ErrorMessage(ex.getMessage(), HttpStatus.FORBIDDEN.value(), Instant.now());
    }

    // todo - for testing populate message
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage handleException(Exception ex) {
        return new ErrorMessage(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), Instant.now());
    }
}