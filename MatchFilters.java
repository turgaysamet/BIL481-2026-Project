package com.campusbridge.matching.core;

import com.campusbridge.auth.UserType;
import com.campusbridge.profile.InterestCategory;

/**
 * User-controlled filters (FR-07). A null field means "no filter".
 *
 * @param sameGenderOnly   only candidates who stated the same gender as the requester
 * @param targetLanguage   ISO 639-1 code the requester wants to practise with the candidate
 * @param interestCategory candidate must have at least one interest in this category
 * @param userType         ERASMUS or LOCAL
 */
public record MatchFilters(boolean sameGenderOnly, String targetLanguage,
                           InterestCategory interestCategory, UserType userType) {

    public static MatchFilters none() {
        return new MatchFilters(false, null, null, null);
    }
}
