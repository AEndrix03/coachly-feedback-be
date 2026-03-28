package com.coachly.feedback.common.model;

public interface NotificationHook {
    void notify(String topic, Object payload);
}