package com.campusbridge.event;

import com.campusbridge.auth.User;
import com.campusbridge.common.BusinessRuleException;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/** Activity organised by a student (FR-09 – FR-11). */
@Entity
@Table(name = "events")
public class Event {

    public static final int MIN_CAPACITY = 2;
    public static final int MAX_CAPACITY = 100;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User creator;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventCategory category;

    @Column(nullable = false)
    private Instant startsAt;

    @Column(nullable = false)
    private String location;

    private int capacity;
    private int participantCount;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "event_languages")
    private Set<String> languages = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus status = EventStatus.SCHEDULED;

    /** Optimistic locking: two users cannot take the last free place at the same time. */
    @Version
    private long version;

    protected Event() {
        // required by JPA
    }

    public Event(User creator, String title, String description, EventCategory category, Instant startsAt,
                 String location, int capacity, Set<String> languages, Instant now) {
        if (!startsAt.isAfter(now)) {
            throw new BusinessRuleException("INVALID_EVENT", "The event date must be in the future");
        }
        if (capacity < MIN_CAPACITY || capacity > MAX_CAPACITY) {
            throw new BusinessRuleException("INVALID_EVENT", "Capacity must be between 2 and 100");
        }
        this.creator = creator;
        this.title = title;
        this.description = description;
        this.category = category;
        this.startsAt = startsAt;
        this.location = location;
        this.capacity = capacity;
        this.languages = new HashSet<>(languages);
    }

    public boolean isOpenForChanges(Instant now) {
        return status == EventStatus.SCHEDULED && now.isBefore(startsAt);
    }

    public int freePlaces() {
        return capacity - participantCount;
    }

    /** FR-11: increases the participant counter if there is a free place. */
    public void addParticipant(Instant now) {
        if (!isOpenForChanges(now)) {
            throw new BusinessRuleException("EVENT_CLOSED", "This event is cancelled or has already started");
        }
        if (freePlaces() <= 0) {
            throw new BusinessRuleException("EVENT_FULL", "This event is full");
        }
        participantCount++;
    }

    public void removeParticipant(Instant now) {
        if (!isOpenForChanges(now)) {
            throw new BusinessRuleException("EVENT_CLOSED", "You can only leave an event before it starts");
        }
        participantCount--;
    }

    public void cancel(Long actingUserId, Instant now) {
        if (!creator.getId().equals(actingUserId)) {
            throw new BusinessRuleException("NOT_ALLOWED", "Only the creator can cancel the event");
        }
        if (!isOpenForChanges(now)) {
            throw new BusinessRuleException("EVENT_CLOSED", "This event cannot be cancelled any more");
        }
        status = EventStatus.CANCELLED;
    }

    public Long getId() {
        return id;
    }

    public User getCreator() {
        return creator;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public EventCategory getCategory() {
        return category;
    }

    public Instant getStartsAt() {
        return startsAt;
    }

    public String getLocation() {
        return location;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getParticipantCount() {
        return participantCount;
    }

    public Set<String> getLanguages() {
        return languages;
    }

    public EventStatus getStatus() {
        return status;
    }
}
