package com.campusbridge.common;

/** Sends in-app notifications (FR-15). Full implementation is planned for Sprint 2. */
public interface NotificationPublisher {

    void notifyUser(Long userId, String type, String message);
}
