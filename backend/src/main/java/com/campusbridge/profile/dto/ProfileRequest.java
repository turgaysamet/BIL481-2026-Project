package com.campusbridge.profile.dto;

import com.campusbridge.profile.Gender;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Set;

/** Body of PUT /api/profiles/me (FR-04, FR-05). */
public record ProfileRequest(
        @NotBlank @Size(max = 50) String displayName,
        @Size(max = 100) String homeUniversity,
        @Size(max = 60) String homeCountry,
        Gender gender,
        @Size(max = 500) String bio,
        @NotEmpty @Valid List<SpokenLanguageDto> spokenLanguages,
        Set<String> learningLanguages,
        @NotNull @Size(min = 3, message = "select at least 3 interests") Set<Long> interestIds,
        Set<String> skills) {
}
