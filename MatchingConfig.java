package com.campusbridge.matching;

import com.campusbridge.matching.core.CompatibilityCalculator;
import com.campusbridge.matching.core.MatchFilterPolicy;
import com.campusbridge.matching.core.MatchSuggestionEngine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Registers the framework-free matching core as Spring beans. */
@Configuration
public class MatchingConfig {

    @Bean
    public MatchSuggestionEngine matchSuggestionEngine() {
        return new MatchSuggestionEngine(new CompatibilityCalculator(), new MatchFilterPolicy());
    }
}
