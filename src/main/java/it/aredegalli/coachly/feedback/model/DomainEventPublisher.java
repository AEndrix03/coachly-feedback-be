package it.aredegalli.coachly.feedback.model;

public interface DomainEventPublisher {
    void publish(Object event);
}


