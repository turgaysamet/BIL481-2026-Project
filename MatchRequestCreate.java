package com.campusbridge.matching.dto;

import jakarta.validation.constraints.NotNull;

public record MatchRequestCreate(@NotNull Long receiverId) {
}
