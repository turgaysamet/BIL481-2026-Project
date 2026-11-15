package com.campusbridge.matching;

import java.util.EnumSet;
import java.util.Set;

/**
 * States of a match request (FR-08). Allowed transitions (state diagram in the Design Document):
 * PENDING → ACCEPTED | DECLINED | CANCELLED, ACCEPTED → REMOVED. DECLINED, CANCELLED and REMOVED are final.
 */
public enum MatchRequestStatus {
    PENDING,
    ACCEPTED,
    DECLINED,
    CANCELLED,
    REMOVED;

    public Set<MatchRequestStatus> allowedNext() {
        return switch (this) {
            case PENDING -> EnumSet.of(ACCEPTED, DECLINED, CANCELLED);
            case ACCEPTED -> EnumSet.of(REMOVED);
            default -> EnumSet.noneOf(MatchRequestStatus.class);
        };
    }

    public boolean canTransitionTo(MatchRequestStatus next) {
        return allowedNext().contains(next);
    }
}
