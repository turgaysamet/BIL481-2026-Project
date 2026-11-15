package com.campusbridge.event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.campusbridge.auth.User;
import com.campusbridge.auth.UserRepository;
import com.campusbridge.common.BusinessRuleException;
import com.campusbridge.common.NotificationPublisher;
import com.campusbridge.event.dto.CreateEventRequest;
import com.campusbridge.event.dto.EventResponse;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EventServiceTest {

    private static final Instant NOW = Instant.parse("2026-11-15T10:00:00Z");
    private static final Instant TOMORROW = NOW.plusSeconds(86400);

    private EventRepository events;
    private EventParticipationRepository participations;
    private UserRepository users;
    private NotificationPublisher notifications;
    private EventService service;
    private User creator;
    private User student;

    @BeforeEach
    void setUp() {
        events = mock(EventRepository.class);
        participations = mock(EventParticipationRepository.class);
        users = mock(UserRepository.class);
        notifications = mock(NotificationPublisher.class);
        service = new EventService(events, participations, users, notifications, Clock.fixed(NOW, ZoneOffset.UTC));
        creator = user(1L);
        student = user(2L);
        when(users.findById(1L)).thenReturn(Optional.of(creator));
        when(users.findById(2L)).thenReturn(Optional.of(student));
        when(events.save(any(Event.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private static User user(Long id) {
        User user = mock(User.class);
        when(user.getId()).thenReturn(id);
        return user;
    }

    private Event existingEvent(int capacity) {
        Event event = new Event(creator, "Football match", "5 vs 5", EventCategory.SPORT, TOMORROW,
                "TOBB ETÜ sports hall", capacity, Set.of("en", "tr"), NOW);
        event.addParticipant(NOW); // the creator
        when(events.findById(10L)).thenReturn(Optional.of(event));
        return event;
    }

    @Test
    void createdEventCountsCreatorAsParticipant() {
        CreateEventRequest request = new CreateEventRequest("Tandem TR-ES", "Coffee and conversation",
                EventCategory.LANGUAGE, TOMORROW, "Library café", 6, Set.of("tr", "es"));

        EventResponse response = service.create(1L, request);

        assertThat(response.participantCount()).isEqualTo(1);
        assertThat(response.freePlaces()).isEqualTo(5);
        verify(participations).save(any(EventParticipation.class));
    }

    @Test
    void eventInThePastIsRejected() {
        CreateEventRequest request = new CreateEventRequest("Old event", null, EventCategory.OTHER,
                NOW.minusSeconds(60), "Campus", 10, Set.of("en"));

        assertThatThrownBy(() -> service.create(1L, request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("future");
    }

    @Test
    void userCanJoinEventWithFreePlaces() {
        existingEvent(3);
        when(participations.existsByEventIdAndUserId(10L, 2L)).thenReturn(false);

        EventResponse response = service.join(2L, 10L);

        assertThat(response.participantCount()).isEqualTo(2);
        assertThat(response.joinedByMe()).isTrue();
    }

    @Test
    void joiningFullEventIsRejected() {
        existingEvent(2);
        when(participations.existsByEventIdAndUserId(10L, 2L)).thenReturn(false);
        service.join(2L, 10L); // last free place
        User third = user(3L);
        when(users.findById(3L)).thenReturn(Optional.of(third));

        assertThatThrownBy(() -> service.join(3L, 10L))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("full");
    }

    @Test
    void cannotJoinTwice() {
        existingEvent(5);
        when(participations.existsByEventIdAndUserId(10L, 2L)).thenReturn(true);

        assertThatThrownBy(() -> service.join(2L, 10L)).isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void cancellingNotifiesParticipantsExceptCreator() {
        existingEvent(5);
        when(participations.findByEventId(10L)).thenReturn(List.of(
                new EventParticipation(null, creator, NOW), new EventParticipation(null, student, NOW)));

        service.cancel(1L, 10L);

        verify(notifications).notifyUser(eq(2L), eq("EVENT_CANCELLED"), anyString());
        verify(notifications, never()).notifyUser(eq(1L), anyString(), anyString());
    }

    @Test
    void onlyCreatorCanCancel() {
        existingEvent(5);

        assertThatThrownBy(() -> service.cancel(2L, 10L)).isInstanceOf(BusinessRuleException.class);
        verify(notifications, never()).notifyUser(anyLong(), anyString(), anyString());
    }
}
