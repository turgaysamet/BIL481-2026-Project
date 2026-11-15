package com.campusbridge.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;

/** Single-use code created by a verified local student (FR-02). */
@Entity
@Table(name = "invitation_codes")
public class InvitationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 12)
    private String code;

    @ManyToOne(optional = false)
    private User createdBy;

    @OneToOne
    private User usedBy;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant expiresAt;

    private Instant usedAt;

    protected InvitationCode() {
        // required by JPA
    }

    public InvitationCode(String code, User createdBy, Instant createdAt, Instant expiresAt) {
        this.code = code;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    public boolean isUsable(Instant now) {
        return usedAt == null && now.isBefore(expiresAt);
    }

    public void markUsed(User user, Instant now) {
        this.usedBy = user;
        this.usedAt = now;
    }

    public String getCode() {
        return code;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Instant getUsedAt() {
        return usedAt;
    }
}
