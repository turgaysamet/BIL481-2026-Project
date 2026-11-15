package com.campusbridge.event.dto;

import com.campusbridge.event.EventCategory;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.Set;

/** Body of POST /api/events (FR-09). */
public record CreateEventRequest(
        @NotBlank @Size(max = 100) String title,
        @Size(max = 1000) String description,
        @NotNull EventCategory category,
        @NotNull @Future Instant startsAt,
        @NotBlank String location,
        @Min(2) @Max(100) int capacity,
        @NotEmpty Set<String> languages) {
}
