package com.campusbridge.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Decides whether an e-mail belongs to the university (FR-01). */
@Component
public class EmailDomainPolicy {

    private final String institutionalDomain;

    public EmailDomainPolicy(@Value("${campusbridge.auth.institutional-domain}") String institutionalDomain) {
        this.institutionalDomain = institutionalDomain.toLowerCase();
    }

    public boolean isInstitutional(String email) {
        return email != null && email.toLowerCase().endsWith("@" + institutionalDomain);
    }
}
