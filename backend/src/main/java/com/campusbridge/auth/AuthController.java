package com.campusbridge.auth;

import com.campusbridge.auth.dto.AuthResponse;
import com.campusbridge.auth.dto.InvitationCodeResponse;
import com.campusbridge.auth.dto.LoginRequest;
import com.campusbridge.auth.dto.RegisterRequest;
import com.campusbridge.auth.dto.RegisterResponse;
import com.campusbridge.common.CurrentUserProvider;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** REST endpoints of UC-1 "Register and log in". */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final CurrentUserProvider currentUser;

    public AuthController(AuthService authService, CurrentUserProvider currentUser) {
        this.authService = authService;
        this.currentUser = currentUser;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @GetMapping("/verify")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void verify(@RequestParam String token) {
        authService.verifyEmail(token);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/invitation-codes")
    @ResponseStatus(HttpStatus.CREATED)
    public InvitationCodeResponse createInvitationCode() {
        return authService.createInvitationCode(currentUser.currentUserId());
    }
}
