package com.coachly.feedback.common.model;

import org.springframework.stereotype.Component;

@Component
public class NoOpNotificationHook implements NotificationHook {
    @Override
    public void notify(String topic, Object payload) {
        // No-op extension point for future notification service integration.
    }
}