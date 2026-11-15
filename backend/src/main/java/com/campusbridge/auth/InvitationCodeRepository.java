package com.campusbridge.auth;

import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvitationCodeRepository extends JpaRepository<InvitationCode, Long> {

    Optional<InvitationCode> findByCode(String code);

    /** Number of active (unused and not expired) codes of a local student. */
    long countByCreatedByIdAndUsedAtIsNullAndExpiresAtAfter(Long creatorId, Instant now);
}
