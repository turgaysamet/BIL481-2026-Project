package com.campusbridge.matching;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class MatchRequestStatusTest {

    @Test
    void pendingCanBeAcceptedDeclinedOrCancelled() {
        assertThat(MatchRequestStatus.PENDING.allowedNext())
                .containsExactlyInAnyOrder(MatchRequestStatus.ACCEPTED, MatchRequestStatus.DECLINED, MatchRequestStatus.CANCELLED);
    }

    @Test
    void onlyAcceptedMatchesCanBeRemoved() {
        assertThat(MatchRequestStatus.ACCEPTED.canTransitionTo(MatchRequestStatus.REMOVED)).isTrue();
        assertThat(MatchRequestStatus.PENDING.canTransitionTo(MatchRequestStatus.REMOVED)).isFalse();
    }

    @Test
    void finalStatesHaveNoTransitions() {
        assertThat(MatchRequestStatus.DECLINED.allowedNext()).isEmpty();
        assertThat(MatchRequestStatus.CANCELLED.allowedNext()).isEmpty();
        assertThat(MatchRequestStatus.REMOVED.allowedNext()).isEmpty();
    }
}
