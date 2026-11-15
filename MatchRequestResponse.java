package com.campusbridge.matching.dto;

import com.campusbridge.matching.MatchRequestStatus;
import java.time.Instant;

public record MatchRequestResponse(Long id, Long senderId, Long receiverId, MatchRequestStatus status,
                                   Instant createdAt, Instant respondedAt) {
}
