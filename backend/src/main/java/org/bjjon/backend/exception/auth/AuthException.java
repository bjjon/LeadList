package org.bjjon.backend.exception.auth;

import org.springframework.security.authentication.BadCredentialsException;

public class AuthException extends BadCredentialsException {
    public AuthException() {
        super("You are not authorized. Please Login again.");
    }
}