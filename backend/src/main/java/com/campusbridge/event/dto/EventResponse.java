package com.campusbridge.event.dto;

import com.campusbridge.event.EventCategory;
import com.campusbridge.event.EventStatus;
import java.time.Instant;
import java.util.Set;

public record EventResponse(
        Long id,
        String title,
        String description,
        EventCategory category,
        Instant startsAt,
        String location,
        int capacity,
        int participantCount,
        int freePlaces,
        Set<String> languages,
        EventStatus status,
        Long creatorId,
        boolean joinedByMe) {
}
