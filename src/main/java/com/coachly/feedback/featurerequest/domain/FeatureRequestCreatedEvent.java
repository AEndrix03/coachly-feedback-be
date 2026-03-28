package com.coachly.feedback.featurerequest.domain;

import java.util.UUID;

public record FeatureRequestCreatedEvent(UUID featureRequestId, UUID authorUserId) {
}