package com.campusbridge.profile;

import com.campusbridge.auth.UserType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

/** Matching filters saved between sessions (FR-07). */
@Embeddable
public class MatchPreferences {

    @Column(name = "pref_same_gender_only")
    private boolean sameGenderOnly;

    @Column(name = "pref_target_language", length = 2)
    private String targetLanguage;

    @Enumerated(EnumType.STRING)
    @Column(name = "pref_interest_category")
    private InterestCategory interestCategory;

    @Enumerated(EnumType.STRING)
    @Column(name = "pref_user_type")
    private UserType userType;

    public MatchPreferences() {
        // no filters by default
    }

    public MatchPreferences(boolean sameGenderOnly, String targetLanguage,
                            InterestCategory interestCategory, UserType userType) {
        this.sameGenderOnly = sameGenderOnly;
        this.targetLanguage = targetLanguage;
        this.interestCategory = interestCategory;
        this.userType = userType;
    }

    public boolean isSameGenderOnly() {
        return sameGenderOnly;
    }

    public String getTargetLanguage() {
        return targetLanguage;
    }

    public InterestCategory getInterestCategory() {
        return interestCategory;
    }

    public UserType getUserType() {
        return userType;
    }
}
