package com.campusbridge.matching.core;

/** Partial scores in [0,1] and the final compatibility score in [0,100]. */
public record ScoreBreakdown(double interestScore, double skillScore, double languageScore, int total) {
}
