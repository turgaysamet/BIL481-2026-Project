package com.campusbridge.auth.dto;

import java.time.Instant;

public record InvitationCodeResponse(String code, Instant expiresAt) {
}
