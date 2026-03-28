package it.aredegalli.coachly.feedback.model;

public interface NotificationHook {
    void notify(String topic, Object payload);
}


