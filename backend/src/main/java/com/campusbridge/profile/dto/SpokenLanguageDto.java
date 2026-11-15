package com.campusbridge.profile.dto;

import com.campusbridge.profile.CefrLevel;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record SpokenLanguageDto(@NotNull @Pattern(regexp = "[a-z]{2}") String languageCode, @NotNull CefrLevel level) {
}
