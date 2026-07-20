package org.bjjon.backend.dto.lead;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LeadRequest(
        @NotBlank(message = "The firstname can not be blank")
        String firstname,
        @NotBlank(message = "The lastname can not be blank")
        String lastname,
        @NotBlank(message = "The company name can not be blank")
        String company,
        @NotBlank(message = "The phone number can not be blank")
        String phone,
        @NotBlank(message = "Email can not be blank")
        @Email(message = "Invalid email pattern")
        String email,
        @NotNull(message = "A note can not be null")
        String note
) {
}
