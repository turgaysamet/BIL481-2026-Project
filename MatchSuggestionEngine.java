package com.campusbridge.matching.core;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * Core algorithm of UC-3: filter → score → rank → top N.
 * Complexity: O(n · k) for n candidates and k = size of the interest/skill/language sets,
 * plus O(n log n) for sorting, which is fast enough for a university-sized user base.
 */
public class MatchSuggestionEngine {

    public static final int DEFAULT_LIMIT = 10;

    private final CompatibilityCalculator calculator;
    private final MatchFilterPolicy filterPolicy;

    public MatchSuggestionEngine(CompatibilityCalculator calculator, MatchFilterPolicy filterPolicy) {
        this.calculator = calculator;
        this.filterPolicy = filterPolicy;
    }

    /**
     * @param requester       the student asking for suggestions
     * @param candidates      complete profiles of active users
     * @param excludedUserIds users already matched / with a pending request / who declined recently
     * @param filters         FR-07 filters
     * @param limit           maximum number of suggestions (10 in FR-06)
     */
    public List<MatchSuggestion> suggest(CandidateProfile requester, Collection<CandidateProfile> candidates,
                                         Set<Long> excludedUserIds, MatchFilters filters, int limit) {
        return candidates.stream()
                .filter(c -> c.userId() != requester.userId())
                .filter(c -> !excludedUserIds.contains(c.userId()))
                .filter(c -> filterPolicy.accepts(requester, c, filters))
                .map(c -> new MatchSuggestion(c.userId(), calculator.score(requester, c)))
                .filter(s -> s.score().total() > 0)
                .sorted(Comparator
                        .comparingInt((MatchSuggestion s) -> s.score().total()).reversed()
                        .thenComparing(Comparator.comparingDouble((MatchSuggestion s) -> s.score().interestScore()).reversed())
                        .thenComparingLong(MatchSuggestion::userId))
                .limit(limit)
                .toList();
    }
}
