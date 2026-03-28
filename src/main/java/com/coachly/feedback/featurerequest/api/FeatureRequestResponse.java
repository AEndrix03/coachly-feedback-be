package com.coachly.feedback.featurerequest.api;

import com.coachly.feedback.common.model.FeatureRequestStatus;

import java.time.Instant;
import java.util.UUID;

public record FeatureRequestResponse(
        UUID id,
        UUID authorUserId,
        String title,
        String description,
        String category,
        FeatureRequestStatus status,
        UUID duplicateOfId,
        String platformTarget,
        String moduleKey,
        int upvotesCount,
        int commentsCount,
        Instant createdAt,
        Instant updatedAt
) {
}