package com.campusbridge.matching.core;

/** Applies the user-controlled filters of FR-07 to one candidate. */
public class MatchFilterPolicy {

    public boolean accepts(CandidateProfile requester, CandidateProfile candidate, MatchFilters filters) {
        if (filters.sameGenderOnly()
                && (requester.gender() == null || candidate.gender() == null || requester.gender() != candidate.gender())) {
            return false;
        }
        if (filters.targetLanguage() != null
                && !CompatibilityCalculator.speaksAtExchangeLevel(candidate.spokenLanguages(), filters.targetLanguage())) {
            return false;
        }
        if (filters.interestCategory() != null && !candidate.interestCategories().contains(filters.interestCategory())) {
            return false;
        }
        return filters.userType() == null || candidate.userType() == filters.userType();
    }
}
