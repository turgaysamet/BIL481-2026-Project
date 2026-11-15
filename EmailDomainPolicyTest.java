package com.campusbridge.auth;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class EmailDomainPolicyTest {

    private final EmailDomainPolicy policy = new EmailDomainPolicy("etu.edu.tr");

    @Test
    void acceptsInstitutionalEmailIgnoringCase() {
        assertThat(policy.isInstitutional("apolo@etu.edu.tr")).isTrue();
        assertThat(policy.isInstitutional("APOLO@ETU.EDU.TR")).isTrue();
    }

    @Test
    void rejectsOtherDomainsAndLookalikes() {
        assertThat(policy.isInstitutional("someone@gmail.com")).isFalse();
        assertThat(policy.isInstitutional("someone@fake-etu.edu.tr")).isFalse();
        assertThat(policy.isInstitutional(null)).isFalse();
    }
}
