package org.bjjon.backend.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record LoginRequest(
        @NotBlank(message = "Email can not be blank")
        @Email(message = "Invalid email pattern")
        String email,
        @NotBlank(message = "Password can not be blank")
        @Length(min = 8, message = "The password must be at least min 8 characters long")
        String password
) {}