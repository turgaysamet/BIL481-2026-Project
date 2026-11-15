package com.campusbridge.auth;

/** Lifecycle of an account. See the account state diagram in the Design Document (UC-1). */
public enum AccountStatus {
    PENDING_VERIFICATION,
    ACTIVE,
    SUSPENDED,
    DELETED
}
