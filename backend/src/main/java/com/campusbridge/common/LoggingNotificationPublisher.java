package com.campusbridge.common;

import java.util.logging.Logger;
import org.springframework.stereotype.Component;

/** Development implementation of {@link NotificationPublisher} that only writes to the log. */
@Component
public class LoggingNotificationPublisher implements NotificationPublisher {

    private static final Logger LOG = Logger.getLogger(LoggingNotificationPublisher.class.getName());

    @Override
    public void notifyUser(Long userId, String type, String message) {
        LOG.info(() -> "[notification] user=" + userId + " type=" + type + " msg=" + message);
    }
}
