package com.campusbridge.profile;

import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/** Loads a default interest catalogue the first time the application starts (demo data). */
@Component
public class InterestCatalogSeeder implements CommandLineRunner {

    private final InterestRepository interests;

    public InterestCatalogSeeder(InterestRepository interests) {
        this.interests = interests;
    }

    @Override
    public void run(String... args) {
        if (interests.count() > 0) {
            return;
        }
        interests.saveAll(List.of(
                new Interest("Football", InterestCategory.SPORT),
                new Interest("Basketball", InterestCategory.SPORT),
                new Interest("Running", InterestCategory.SPORT),
                new Interest("Language exchange", InterestCategory.LANGUAGE),
                new Interest("Turkish cuisine", InterestCategory.FOOD),
                new Interest("Cooking", InterestCategory.FOOD),
                new Interest("Museums & history", InterestCategory.CULTURE),
                new Interest("Cinema", InterestCategory.CULTURE),
                new Interest("Live music", InterestCategory.MUSIC),
                new Interest("Guitar", InterestCategory.MUSIC),
                new Interest("Programming", InterestCategory.TECHNOLOGY),
                new Interest("Video games", InterestCategory.TECHNOLOGY),
                new Interest("Hiking", InterestCategory.OUTDOOR),
                new Interest("Photography", InterestCategory.OTHER)));
    }
}
