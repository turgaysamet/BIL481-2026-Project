package com.campusbridge.matching.core;

import static com.campusbridge.matching.core.TestProfiles.profile;
import static org.assertj.core.api.Assertions.assertThat;

import com.campusbridge.auth.UserType;
import com.campusbridge.profile.CefrLevel;
import com.campusbridge.profile.Gender;
import com.campusbridge.profile.InterestCategory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class MatchSuggestionEngineTest {

    private final MatchSuggestionEngine engine =
            new MatchSuggestionEngine(new CompatibilityCalculator(), new MatchFilterPolicy());

    private final CandidateProfile requester = profile(1, UserType.ERASMUS, Gender.FEMALE, Set.of(1L, 2L, 3L),
            Set.of(), Map.of("es", CefrLevel.C2), Set.of("tr"));

    @Test
    void ordersByScoreAndExcludesZeroScores() {
        CandidateProfile best = profile(2, UserType.LOCAL, Gender.FEMALE, Set.of(1L, 2L, 3L), Set.of(),
                Map.of("tr", CefrLevel.C2), Set.of("es"));
        CandidateProfile medium = profile(3, UserType.LOCAL, Gender.MALE, Set.of(1L), Set.of(),
                Map.of("tr", CefrLevel.C2), Set.of());
        CandidateProfile nothingInCommon = profile(4, UserType.LOCAL, Gender.MALE, Set.of(7L), Set.of(),
                Map.of("de", CefrLevel.C2), Set.of());

        List<MatchSuggestion> result = engine.suggest(requester, List.of(medium, nothingInCommon, best),
                Set.of(), MatchFilters.none(), 10);

        assertThat(result).extracting(MatchSuggestion::userId).containsExactly(2L, 3L);
    }

    @Test
    void sameGenderFilterRemovesOtherAndUnknownGenders() {
        CandidateProfile female = profile(2, UserType.LOCAL, Gender.FEMALE, Set.of(1L), Set.of(), Map.of(), Set.of());
        CandidateProfile male = profile(3, UserType.LOCAL, Gender.MALE, Set.of(1L), Set.of(), Map.of(), Set.of());
        CandidateProfile unknown = profile(4, UserType.LOCAL, null, Set.of(1L), Set.of(), Map.of(), Set.of());

        List<MatchSuggestion> result = engine.suggest(requester, List.of(female, male, unknown), Set.of(),
                new MatchFilters(true, null, null, null), 10);

        assertThat(result).extracting(MatchSuggestion::userId).containsExactly(2L);
    }

    @Test
    void targetLanguageCategoryAndUserTypeFiltersAreApplied() {
        CandidateProfile turkishSpeaker = profile(2, UserType.LOCAL, Gender.MALE, Set.of(1L), Set.of(),
                Map.of("tr", CefrLevel.B1), Set.of());
        CandidateProfile englishSpeaker = profile(3, UserType.LOCAL, Gender.MALE, Set.of(1L), Set.of(),
                Map.of("en", CefrLevel.C2), Set.of());
        CandidateProfile erasmus = profile(4, UserType.ERASMUS, Gender.MALE, Set.of(1L), Set.of(),
                Map.of("tr", CefrLevel.C1), Set.of());

        assertThat(engine.suggest(requester, List.of(turkishSpeaker, englishSpeaker, erasmus), Set.of(),
                new MatchFilters(false, "tr", null, UserType.LOCAL), 10))
                .extracting(MatchSuggestion::userId).containsExactly(2L);

        MatchFilterPolicy policy = new MatchFilterPolicy();
        assertThat(policy.accepts(requester, TestProfiles.withCategories(5, Set.of(InterestCategory.MUSIC)),
                new MatchFilters(false, null, InterestCategory.SPORT, null))).isFalse();
    }

    @Test
    void excludedUsersAndRequesterNeverAppear() {
        CandidateProfile alreadyMatched = profile(2, UserType.LOCAL, Gender.FEMALE, Set.of(1L, 2L, 3L), Set.of(),
                Map.of("tr", CefrLevel.C2), Set.of("es"));

        List<MatchSuggestion> result = engine.suggest(requester, List.of(alreadyMatched, requester), Set.of(2L),
                MatchFilters.none(), 10);

        assertThat(result).isEmpty();
    }

    @Test
    void returnsAtMostTheLimit() {
        List<CandidateProfile> many = new ArrayList<>();
        for (long id = 2; id < 30; id++) {
            many.add(profile(id, UserType.LOCAL, Gender.FEMALE, Set.of(1L), Set.of(), Map.of(), Set.of()));
        }

        assertThat(engine.suggest(requester, many, Set.of(), MatchFilters.none(), MatchSuggestionEngine.DEFAULT_LIMIT))
                .hasSize(10)
                .extracting(MatchSuggestion::userId)
                .startsWith(2L, 3L); // ties are broken by user id
    }
}
