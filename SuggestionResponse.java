package com.campusbridge.matching.dto;

import com.campusbridge.matching.core.ScoreBreakdown;
import com.campusbridge.profile.dto.ProfileResponse;

/** One entry of GET /api/matches/suggestions: public profile + compatibility score. */
public record SuggestionResponse(ProfileResponse profile, ScoreBreakdown score) {
}
