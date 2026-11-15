package com.campusbridge.matching.core;

import com.campusbridge.profile.CefrLevel;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Computes the compatibility score between two students (FR-06).
 *
 * <pre>
 * total = round(100 * (0.40 * interestScore + 0.20 * skillScore + 0.40 * languageScore))
 *
 * interestScore = |interestsA ∩ interestsB| / |interestsA ∪ interestsB|     (Jaccard index)
 * skillScore    = |skillsA ∩ skillsB| / |skillsA ∪ skillsB|                 (Jaccard index)
 * languageScore = (exchange(A→B) + exchange(B→A)) / 2
 *   exchange(X→Y) = 1 if X speaks, at level B1 or higher, a language Y wants to practise, else 0
 * </pre>
 *
 * Nationality is never used (FR-06).
 */
public class CompatibilityCalculator {

    public static final double INTEREST_WEIGHT = 0.40;
    public static final double SKILL_WEIGHT = 0.20;
    public static final double LANGUAGE_WEIGHT = 0.40;
    public static final CefrLevel MIN_EXCHANGE_LEVEL = CefrLevel.B1;

    public ScoreBreakdown score(CandidateProfile a, CandidateProfile b) {
        double interest = jaccard(a.interestIds(), b.interestIds());
        double skill = jaccard(normalize(a.skills()), normalize(b.skills()));
        double language = (exchange(a, b) + exchange(b, a)) / 2.0;
        int total = (int) Math.round(100 * (INTEREST_WEIGHT * interest + SKILL_WEIGHT * skill + LANGUAGE_WEIGHT * language));
        return new ScoreBreakdown(interest, skill, language, total);
    }

    static <T> double jaccard(Set<T> x, Set<T> y) {
        if (x.isEmpty() && y.isEmpty()) {
            return 0.0;
        }
        Set<T> intersection = new HashSet<>(x);
        intersection.retainAll(y);
        Set<T> union = new HashSet<>(x);
        union.addAll(y);
        return (double) intersection.size() / union.size();
    }

    /** 1 if "teacher" can help "learner" practise one of the languages the learner wants. */
    static double exchange(CandidateProfile teacher, CandidateProfile learner) {
        for (String wanted : learner.learningLanguages()) {
            if (speaksAtExchangeLevel(teacher.spokenLanguages(), wanted)) {
                return 1.0;
            }
        }
        return 0.0;
    }

    static boolean speaksAtExchangeLevel(Map<String, CefrLevel> spoken, String language) {
        CefrLevel level = spoken.get(language);
        return level != null && level.isAtLeast(MIN_EXCHANGE_LEVEL);
    }

    private static Set<String> normalize(Set<String> values) {
        Set<String> result = new HashSet<>();
        for (String v : values) {
            result.add(v.trim().toLowerCase());
        }
        return result;
    }
}
