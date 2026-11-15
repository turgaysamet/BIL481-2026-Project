package com.campusbridge.event;

/** SCHEDULED → CANCELLED (by creator) or FINISHED (after start time). "Full" is derived, not stored. */
public enum EventStatus {
    SCHEDULED,
    CANCELLED,
    FINISHED
}
