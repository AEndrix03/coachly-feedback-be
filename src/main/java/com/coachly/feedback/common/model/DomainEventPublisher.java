package com.coachly.feedback.common.model;

public interface DomainEventPublisher {
    void publish(Object event);
}