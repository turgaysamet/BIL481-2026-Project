package com.campusbridge.matching.core;

import static com.campusbridge.matching.core.TestProfiles.profile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import com.campusbridge.auth.UserType;
import com.campusbridge.profile.CefrLevel;
import com.campusbridge.profile.Gender;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CompatibilityCalculatorTest {

    private final CompatibilityCalculator calculator = new CompatibilityCalculator();

    @Test
    void perfectLanguageExchangeAndSameInterestsGivesHighScore() {
        // Erasmus student speaks Spanish and wants Turkish; local student speaks Turkish and wants Spanish
        CandidateProfile erasmus = profile(1, UserType.ERASMUS, Gender.MALE, Set.of(1L, 2L, 3L),
                Set.of("guitar"), Map.of("es", CefrLevel.C2), Set.of("tr"));
        CandidateProfile local = profile(2, UserType.LOCAL, Gender.MALE, Set.of(1L, 2L, 3L),
                Set.of("Guitar "), Map.of("tr", CefrLevel.C2), Set.of("es"));

        ScoreBreakdown score = calculator.score(erasmus, local);

        assertThat(score.interestScore()).isEqualTo(1.0);
        assertThat(score.skillScore()).isEqualTo(1.0); // skills are compared case-insensitively
        assertThat(score.languageScore()).isEqualTo(1.0);
        assertThat(score.total()).isEqualTo(100);
    }

    @Test
    void scoreUsesDocumentedWeights() {
        // 2 shared interests out of 4 different ones -> 0.5; no skills; one-way language exchange -> 0.5
        CandidateProfile a = profile(1, UserType.ERASMUS, null, Set.of(1L, 2L, 3L), Set.of(),
                Map.of("en", CefrLevel.B2), Set.of("tr"));
        CandidateProfile b = profile(2, UserType.LOCAL, null, Set.of(2L, 3L, 4L), Set.of(),
                Map.of("tr", CefrLevel.C2), Set.of());

        ScoreBreakdown score = calculator.score(a, b);

        assertThat(score.interestScore()).isCloseTo(0.5, within(1e-9));
        assertThat(score.languageScore()).isEqualTo(0.5);
        // 100 * (0.4*0.5 + 0.2*0 + 0.4*0.5) = 40
        assertThat(score.total()).isEqualTo(40);
    }

    @Test
    void languageBelowB1DoesNotCountForExchange() {
        CandidateProfile beginner = profile(1, UserType.LOCAL, null, Set.of(9L), Set.of(),
                Map.of("es", CefrLevel.A2), Set.of());
        CandidateProfile learner = profile(2, UserType.ERASMUS, null, Set.of(8L), Set.of(),
                Map.of("en", CefrLevel.C1), Set.of("es"));

        assertThat(calculator.score(beginner, learner).languageScore()).isZero();
    }

    @Test
    void scoreIsSymmetric() {
        CandidateProfile a = profile(1, UserType.ERASMUS, null, Set.of(1L, 5L), Set.of("chess"),
                Map.of("it", CefrLevel.C2), Set.of("tr"));
        CandidateProfile b = profile(2, UserType.LOCAL, null, Set.of(1L), Set.of("chess", "piano"),
                Map.of("tr", CefrLevel.C2), Set.of("it"));

        assertThat(calculator.score(a, b).total()).isEqualTo(calculator.score(b, a).total());
    }
}
