package com.campusbridge.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/** Account of a student or administrator (FR-01, FR-02, FR-03). */
@Entity
@Table(name = "users")
public class User {

    public static final int MAX_FAILED_LOGINS = 5;
    public static final long LOCK_MINUTES = 15;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType userType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.STUDENT;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status = AccountStatus.PENDING_VERIFICATION;

    private String verificationToken;
    private Instant verificationExpiresAt;
    private int failedLoginAttempts;
    private Instant lockedUntil;

    @Column(nullable = false)
    private Instant consentAcceptedAt;

    @Column(nullable = false)
    private Instant createdAt;

    protected User() {
        // required by JPA
    }

    public User(String email, String passwordHash, UserType userType, Instant now) {
        this.email = email.toLowerCase();
        this.passwordHash = passwordHash;
        this.userType = userType;
        this.consentAcceptedAt = now;
        this.createdAt = now;
    }

    /** FR-01: the account becomes active only after the verification link is opened. */
    public void startVerification(String token, Instant expiresAt) {
        this.verificationToken = token;
        this.verificationExpiresAt = expiresAt;
    }

    public boolean verify(String token, Instant now) {
        if (status != AccountStatus.PENDING_VERIFICATION || verificationToken == null
                || !verificationToken.equals(token) || now.isAfter(verificationExpiresAt)) {
            return false;
        }
        status = AccountStatus.ACTIVE;
        verificationToken = null;
        verificationExpiresAt = null;
        return true;
    }

    public boolean isLocked(Instant now) {
        return lockedUntil != null && now.isBefore(lockedUntil);
    }

    /** FR-03: lock the account for 15 minutes after 5 consecutive failed logins. */
    public void registerFailedLogin(Instant now) {
        failedLoginAttempts++;
        if (failedLoginAttempts >= MAX_FAILED_LOGINS) {
            lockedUntil = now.plusSeconds(LOCK_MINUTES * 60);
            failedLoginAttempts = 0;
        }
    }

    public void registerSuccessfulLogin() {
        failedLoginAttempts = 0;
        lockedUntil = null;
    }

    public boolean isActive() {
        return status == AccountStatus.ACTIVE;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public UserType getUserType() {
        return userType;
    }

    public UserRole getRole() {
        return role;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public String getVerificationToken() {
        return verificationToken;
    }
}
