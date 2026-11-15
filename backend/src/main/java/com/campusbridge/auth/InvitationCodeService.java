package com.campusbridge.auth;

import com.campusbridge.common.BusinessRuleException;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Business rules of invitation codes (FR-02). */
@Service
public class InvitationCodeService {

    public static final int MAX_ACTIVE_CODES = 5;
    public static final Duration VALIDITY = Duration.ofDays(14);
    private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // no 0/O, 1/I
    private static final int CODE_LENGTH = 8;

    private final InvitationCodeRepository repository;
    private final Clock clock;
    private final SecureRandom random = new SecureRandom();

    public InvitationCodeService(InvitationCodeRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    @Transactional
    public InvitationCode generate(User creator) {
        if (creator.getUserType() != UserType.LOCAL || !creator.isActive()) {
            throw new BusinessRuleException("NOT_ALLOWED", "Only verified local students can create invitation codes");
        }
        Instant now = clock.instant();
        long active = repository.countByCreatedByIdAndUsedAtIsNullAndExpiresAtAfter(creator.getId(), now);
        if (active >= MAX_ACTIVE_CODES) {
            throw new BusinessRuleException("CODE_LIMIT", "A student can have at most " + MAX_ACTIVE_CODES + " active codes");
        }
        return repository.save(new InvitationCode(randomCode(), creator, now, now.plus(VALIDITY)));
    }

    /** Validates the code and marks it as used by the new user. */
    @Transactional
    public void consume(String code, User newUser) {
        Instant now = clock.instant();
        InvitationCode invitation = repository.findByCode(code.trim().toUpperCase())
                .filter(c -> c.isUsable(now))
                .orElseThrow(() -> new BusinessRuleException("INVALID_CODE", "Invitation code is invalid, used or expired"));
        invitation.markUsed(newUser, now);
    }

    private String randomCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }
}
