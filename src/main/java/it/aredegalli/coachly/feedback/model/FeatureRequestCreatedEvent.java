package it.aredegalli.coachly.feedback.model;

import java.util.UUID;

public record FeatureRequestCreatedEvent(UUID featureRequestId, UUID authorUserId) {
}


