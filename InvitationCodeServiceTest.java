package com.campusbridge.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.campusbridge.common.BusinessRuleException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InvitationCodeServiceTest {

    private static final Instant NOW = Instant.parse("2026-11-15T10:00:00Z");

    private InvitationCodeRepository repository;
    private InvitationCodeService service;

    @BeforeEach
    void setUp() {
        repository = mock(InvitationCodeRepository.class);
        when(repository.save(any(InvitationCode.class))).thenAnswer(inv -> inv.getArgument(0));
        service = new InvitationCodeService(repository, Clock.fixed(NOW, ZoneOffset.UTC));
    }

    private static User user(long id, UserType type, boolean active) {
        User user = mock(User.class);
        when(user.getId()).thenReturn(id);
        when(user.getUserType()).thenReturn(type);
        when(user.isActive()).thenReturn(active);
        return user;
    }

    @Test
    void verifiedLocalStudentGetsCodeValidFor14Days() {
        when(repository.countByCreatedByIdAndUsedAtIsNullAndExpiresAtAfter(1L, NOW)).thenReturn(0L);

        InvitationCode code = service.generate(user(1, UserType.LOCAL, true));

        assertThat(code.getCode()).hasSize(8).matches("[A-HJ-NP-Z2-9]+");
        assertThat(code.getExpiresAt()).isEqualTo(NOW.plus(InvitationCodeService.VALIDITY));
    }

    @Test
    void sixthActiveCodeIsRejected() {
        when(repository.countByCreatedByIdAndUsedAtIsNullAndExpiresAtAfter(1L, NOW)).thenReturn(5L);

        assertThatThrownBy(() -> service.generate(user(1, UserType.LOCAL, true)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("at most 5");
        verify(repository, never()).save(any());
    }

    @Test
    void erasmusStudentCannotCreateCodes() {
        assertThatThrownBy(() -> service.generate(user(2, UserType.ERASMUS, true)))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void expiredOrUsedCodeCannotBeConsumed() {
        User creator = user(1, UserType.LOCAL, true);
        InvitationCode expired = new InvitationCode("ABCD2345", creator, NOW.minusSeconds(20 * 86400), NOW.minusSeconds(60));
        when(repository.findByCode("ABCD2345")).thenReturn(Optional.of(expired));

        assertThatThrownBy(() -> service.consume("abcd2345", user(3, UserType.ERASMUS, false)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("invalid, used or expired");
    }

    @Test
    void validCodeIsMarkedAsUsed() {
        InvitationCode valid = new InvitationCode("ABCD2345", user(1, UserType.LOCAL, true), NOW, NOW.plusSeconds(3600));
        when(repository.findByCode("ABCD2345")).thenReturn(Optional.of(valid));

        service.consume(" abcd2345 ", user(3, UserType.ERASMUS, false));

        assertThat(valid.getUsedAt()).isEqualTo(NOW);
        assertThat(valid.isUsable(NOW)).isFalse();
    }
}
