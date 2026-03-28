package com.coachly.feedback.featurerequest.domain;

import java.util.UUID;

public record FeatureRequestVotedEvent(UUID featureRequestId, UUID userId) {
}