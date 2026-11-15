package com.campusbridge.event;

import com.campusbridge.auth.User;
import com.campusbridge.auth.UserRepository;
import com.campusbridge.common.BusinessRuleException;
import com.campusbridge.common.NotFoundException;
import com.campusbridge.common.NotificationPublisher;
import com.campusbridge.event.dto.CreateEventRequest;
import com.campusbridge.event.dto.EventResponse;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Business logic of UC-4 "Create and join an event". */
@Service
public class EventService {

    /** Default search window when no end date is given. */
    static final Duration SEARCH_HORIZON = Duration.ofDays(365);

    private final EventRepository events;
    private final EventParticipationRepository participations;
    private final UserRepository users;
    private final NotificationPublisher notifications;
    private final Clock clock;

    public EventService(EventRepository events, EventParticipationRepository participations, UserRepository users,
                        NotificationPublisher notifications, Clock clock) {
        this.events = events;
        this.participations = participations;
        this.users = users;
        this.notifications = notifications;
        this.clock = clock;
    }

    /** FR-09. The creator automatically takes part in the event. */
    @Transactional
    public EventResponse create(Long creatorId, CreateEventRequest r) {
        Instant now = clock.instant();
        User creator = findUser(creatorId);
        Event event = new Event(creator, r.title(), r.description(), r.category(), r.startsAt(), r.location(),
                r.capacity(), r.languages(), now);
        event.addParticipant(now);
        event = events.save(event);
        participations.save(new EventParticipation(event, creator, now));
        return toResponse(event, true);
    }

    /** FR-10. */
    @Transactional(readOnly = true)
    public List<EventResponse> search(Long viewerId, EventCategory category, Instant from, Instant to,
                                      String language, String keyword) {
        Instant now = clock.instant();
        Instant fromDate = from != null ? from : now;
        Instant toDate = to != null ? to : now.plus(SEARCH_HORIZON);
        String text = keyword == null ? "" : keyword.trim();
        return events.findUpcoming(now, fromDate, toDate, text).stream()
                .filter(e -> category == null || e.getCategory() == category)
                .filter(e -> language == null || e.getLanguages().contains(language))
                .map(e -> toResponse(e, participations.existsByEventIdAndUserId(e.getId(), viewerId)))
                .toList();
    }

    @Transactional(readOnly = true)
    public EventResponse get(Long viewerId, Long eventId) {
        Event event = findEvent(eventId);
        return toResponse(event, participations.existsByEventIdAndUserId(eventId, viewerId));
    }

    /** FR-11: join if there are free places. */
    @Transactional
    public EventResponse join(Long userId, Long eventId) {
        Event event = findEvent(eventId);
        if (participations.existsByEventIdAndUserId(eventId, userId)) {
            throw new BusinessRuleException("ALREADY_JOINED", "You already take part in this event");
        }
        Instant now = clock.instant();
        event.addParticipant(now);
        participations.save(new EventParticipation(event, findUser(userId), now));
        return toResponse(event, true);
    }

    /** FR-11: leave before the event starts. The creator cannot leave (they cancel instead). */
    @Transactional
    public EventResponse leave(Long userId, Long eventId) {
        Event event = findEvent(eventId);
        if (event.getCreator().getId().equals(userId)) {
            throw new BusinessRuleException("NOT_ALLOWED", "The creator cannot leave; cancel the event instead");
        }
        EventParticipation participation = participations.findByEventIdAndUserId(eventId, userId)
                .orElseThrow(() -> new BusinessRuleException("NOT_JOINED", "You do not take part in this event"));
        event.removeParticipant(clock.instant());
        participations.delete(participation);
        return toResponse(event, false);
    }

    /** FR-09: the creator cancels; every participant is notified. */
    @Transactional
    public EventResponse cancel(Long userId, Long eventId) {
        Event event = findEvent(eventId);
        event.cancel(userId, clock.instant());
        for (EventParticipation p : participations.findByEventId(eventId)) {
            if (!p.getUser().getId().equals(userId)) {
                notifications.notifyUser(p.getUser().getId(), "EVENT_CANCELLED", "\"" + event.getTitle() + "\" was cancelled");
            }
        }
        return toResponse(event, true);
    }

    private Event findEvent(Long eventId) {
        return events.findById(eventId).orElseThrow(() -> new NotFoundException("Event not found"));
    }

    private User findUser(Long userId) {
        return users.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
    }

    private static EventResponse toResponse(Event e, boolean joinedByMe) {
        return new EventResponse(e.getId(), e.getTitle(), e.getDescription(), e.getCategory(), e.getStartsAt(),
                e.getLocation(), e.getCapacity(), e.getParticipantCount(), e.freePlaces(), e.getLanguages(),
                e.getStatus(), e.getCreator().getId(), joinedByMe);
    }
}
