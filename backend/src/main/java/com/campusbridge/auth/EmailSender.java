package com.campusbridge.auth;

/** Port to the transactional e-mail service (e.g. Brevo). */
public interface EmailSender {

    void sendVerificationEmail(String to, String verificationLink);
}
