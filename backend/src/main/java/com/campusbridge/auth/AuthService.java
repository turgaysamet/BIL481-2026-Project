package com.campusbridge.auth;

import com.campusbridge.auth.dto.AuthResponse;
import com.campusbridge.auth.dto.InvitationCodeResponse;
import com.campusbridge.auth.dto.LoginRequest;
import com.campusbridge.auth.dto.RegisterRequest;
import com.campusbridge.auth.dto.RegisterResponse;

/** Business interface of the authentication component (UC-1). */
public interface AuthService {

    /** FR-01 / FR-02: creates an inactive account and sends a verification link. */
    RegisterResponse register(RegisterRequest request);

    /** FR-01: activates the account if the token is valid and not expired (24 h). */
    void verifyEmail(String token);

    /** FR-03: returns an access token, or fails after wrong credentials / locked account. */
    AuthResponse login(LoginRequest request);

    /** FR-02: creates a new invitation code for a verified local student. */
    InvitationCodeResponse createInvitationCode(Long userId);
}
