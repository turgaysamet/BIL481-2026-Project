package com.campusbridge.event;

import com.campusbridge.auth.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;

/** A user taking part in an event. The unique constraint prevents joining twice. */
@Entity
@Table(name = "event_participations",
        uniqueConstraints = @UniqueConstraint(columnNames = {"event_id", "user_id"}))
public class EventParticipation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Event event;

    @ManyToOne(optional = false)
    private User user;

    @Column(nullable = false)
    private Instant joinedAt;

    protected EventParticipation() {
        // required by JPA
    }

    public EventParticipation(Event event, User user, Instant joinedAt) {
        this.event = event;
        this.user = user;
        this.joinedAt = joinedAt;
    }

    public User getUser() {
        return user;
    }
}
