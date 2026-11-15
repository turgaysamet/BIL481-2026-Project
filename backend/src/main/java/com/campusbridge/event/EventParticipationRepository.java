package com.campusbridge.event;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventParticipationRepository extends JpaRepository<EventParticipation, Long> {

    boolean existsByEventIdAndUserId(Long eventId, Long userId);

    Optional<EventParticipation> findByEventIdAndUserId(Long eventId, Long userId);

    List<EventParticipation> findByEventId(Long eventId);
}
