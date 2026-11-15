package com.campusbridge.common;

import java.time.Instant;

/** Uniform JSON error body returned by every endpoint. */
public record ApiError(int status, String code, String message, Instant timestamp) {
}
