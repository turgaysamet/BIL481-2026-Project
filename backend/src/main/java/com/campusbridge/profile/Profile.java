package com.campusbridge.profile;

import com.campusbridge.auth.User;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/** Public and matching information of a student (FR-04, FR-05). */
@Entity
@Table(name = "profiles")
public class Profile {

    public static final int MIN_INTERESTS = 3;
    public static final int MAX_BIO_LENGTH = 500;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    private User user;

    @Column(nullable = false)
    private String displayName;

    private String homeUniversity;
    private String homeCountry;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(length = MAX_BIO_LENGTH)
    private String bio;

    private String photoUrl;

    @Column(length = 2)
    private String interfaceLanguage = "en";

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "profile_interests")
    private Set<Interest> interests = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "profile_skills")
    private Set<String> skills = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "profile_spoken_languages")
    private Set<SpokenLanguage> spokenLanguages = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "profile_learning_languages")
    private Set<String> learningLanguages = new HashSet<>();

    @Embedded
    private MatchPreferences matchPreferences = new MatchPreferences();

    private boolean completed;
    private Instant updatedAt;

    protected Profile() {
        // required by JPA
    }

    public Profile(User user) {
        this.user = user;
    }

    /** Replaces the editable data; the profile is complete when it meets the FR-04 rules. */
    public void update(String displayName, String homeUniversity, String homeCountry, Gender gender, String bio,
                       Set<Interest> interests, Set<String> skills, Set<SpokenLanguage> spokenLanguages,
                       Set<String> learningLanguages, Instant now) {
        this.displayName = displayName;
        this.homeUniversity = homeUniversity;
        this.homeCountry = homeCountry;
        this.gender = gender;
        this.bio = bio;
        this.interests = new HashSet<>(interests);
        this.skills = new HashSet<>(skills);
        this.spokenLanguages = new HashSet<>(spokenLanguages);
        this.learningLanguages = new HashSet<>(learningLanguages);
        this.completed = this.interests.size() >= MIN_INTERESTS && !this.spokenLanguages.isEmpty();
        this.updatedAt = now;
    }

    public void setMatchPreferences(MatchPreferences matchPreferences) {
        this.matchPreferences = matchPreferences;
    }

    public void setInterfaceLanguage(String interfaceLanguage) {
        this.interfaceLanguage = interfaceLanguage;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getHomeUniversity() {
        return homeUniversity;
    }

    public String getHomeCountry() {
        return homeCountry;
    }

    public Gender getGender() {
        return gender;
    }

    public String getBio() {
        return bio;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getInterfaceLanguage() {
        return interfaceLanguage;
    }

    public Set<Interest> getInterests() {
        return interests;
    }

    public Set<String> getSkills() {
        return skills;
    }

    public Set<SpokenLanguage> getSpokenLanguages() {
        return spokenLanguages;
    }

    public Set<String> getLearningLanguages() {
        return learningLanguages;
    }

    public MatchPreferences getMatchPreferences() {
        return matchPreferences;
    }

    public boolean isCompleted() {
        return completed;
    }
}
