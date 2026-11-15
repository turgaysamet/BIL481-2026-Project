package com.campusbridge.profile;

/** Common European Framework of Reference language levels (FR-04). */
public enum CefrLevel {
    A1, A2, B1, B2, C1, C2;

    public boolean isAtLeast(CefrLevel other) {
        return this.ordinal() >= other.ordinal();
    }
}
