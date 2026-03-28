package it.aredegalli.coachly.feedback.model;

import java.util.UUID;

public record FeatureRequestVotedEvent(UUID featureRequestId, UUID userId) {
}


