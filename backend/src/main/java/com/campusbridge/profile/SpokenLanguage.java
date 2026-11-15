package com.campusbridge.profile;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.util.Objects;

/** A language the user speaks, with its CEFR level. languageCode uses ISO 639-1 (e.g. "tr", "es"). */
@Embeddable
public class SpokenLanguage {

    @Column(length = 2, nullable = false)
    private String languageCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CefrLevel level;

    protected SpokenLanguage() {
        // required by JPA
    }

    public SpokenLanguage(String languageCode, CefrLevel level) {
        this.languageCode = languageCode.toLowerCase();
        this.level = level;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public CefrLevel getLevel() {
        return level;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof SpokenLanguage other && languageCode.equals(other.languageCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(languageCode);
    }
}
