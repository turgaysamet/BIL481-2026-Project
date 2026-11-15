package com.campusbridge.auth;

import java.util.logging.Logger;
import org.springframework.stereotype.Component;

/** Development implementation: prints the verification link instead of sending an e-mail. */
@Component
public class LoggingEmailSender implements EmailSender {

    private static final Logger LOG = Logger.getLogger(LoggingEmailSender.class.getName());

    @Override
    public void sendVerificationEmail(String to, String verificationLink) {
        LOG.info(() -> "[email] to=" + to + " link=" + verificationLink);
    }
}
