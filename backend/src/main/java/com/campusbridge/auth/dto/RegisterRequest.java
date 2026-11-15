package com.campusbridge.auth.dto;

import com.campusbridge.auth.UserType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Body of POST /api/auth/register.
 * invitationCode is required only when the e-mail is not institutional (FR-02).
 */
public record RegisterRequest(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8, max = 72) String password,
        @NotNull UserType userType,
        String invitationCode,
        @AssertTrue(message = "privacy notice must be accepted") boolean consentAccepted) {
}
