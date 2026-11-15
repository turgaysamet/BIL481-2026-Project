package com.campusbridge.matching;

import com.campusbridge.auth.User;
import com.campusbridge.common.BusinessRuleException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

/** A request from one student to another; an ACCEPTED request is a "match" (FR-08). */
@Entity
@Table(name = "match_requests")
public class MatchRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User sender;

    @ManyToOne(optional = false)
    private User receiver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MatchRequestStatus status = MatchRequestStatus.PENDING;

    @Column(nullable = false)
    private Instant createdAt;

    private Instant respondedAt;

    protected MatchRequest() {
        // required by JPA
    }

    public MatchRequest(User sender, User receiver, Instant now) {
        this.sender = sender;
        this.receiver = receiver;
        this.createdAt = now;
    }

    public void accept(Long actingUserId, Instant now) {
        requireReceiver(actingUserId);
        transitionTo(MatchRequestStatus.ACCEPTED, now);
    }

    public void decline(Long actingUserId, Instant now) {
        requireReceiver(actingUserId);
        transitionTo(MatchRequestStatus.DECLINED, now);
    }

    public void cancel(Long actingUserId, Instant now) {
        if (!sender.getId().equals(actingUserId)) {
            throw new BusinessRuleException("NOT_ALLOWED", "Only the sender can cancel the request");
        }
        transitionTo(MatchRequestStatus.CANCELLED, now);
    }

    public void remove(Long actingUserId, Instant now) {
        if (!involves(actingUserId)) {
            throw new BusinessRuleException("NOT_ALLOWED", "You are not part of this match");
        }
        transitionTo(MatchRequestStatus.REMOVED, now);
    }

    public boolean involves(Long userId) {
        return sender.getId().equals(userId) || receiver.getId().equals(userId);
    }

    public Long otherUserId(Long userId) {
        return sender.getId().equals(userId) ? receiver.getId() : sender.getId();
    }

    private void requireReceiver(Long actingUserId) {
        if (!receiver.getId().equals(actingUserId)) {
            throw new BusinessRuleException("NOT_ALLOWED", "Only the receiver can answer the request");
        }
    }

    private void transitionTo(MatchRequestStatus next, Instant now) {
        if (!status.canTransitionTo(next)) {
            throw new BusinessRuleException("INVALID_STATE", "Cannot change request from " + status + " to " + next);
        }
        status = next;
        respondedAt = now;
    }

    public Long getId() {
        return id;
    }

    public User getSender() {
        return sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public MatchRequestStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getRespondedAt() {
        return respondedAt;
    }
}
