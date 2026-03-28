package it.aredegalli.coachly.feedback.dto;

import it.aredegalli.coachly.feedback.model.ContentVisibility;
import it.aredegalli.coachly.feedback.model.FeedbackStatus;
import it.aredegalli.coachly.feedback.model.FeedbackType;
import it.aredegalli.coachly.feedback.model.IssueSeverity;
import it.aredegalli.coachly.feedback.model.TargetType;

import java.time.Instant;
import java.util.UUID;

public record FeedbackResponse(
        UUID id,
        UUID authorUserId,
        FeedbackType type,
        FeedbackStatus status,
        ContentVisibility visibility,
        String title,
        String body,
        Integer ratingValue,
        String category,
        TargetType targetType,
        UUID targetId,
        String featureKey,
        String screenKey,
        String flowKey,
        String platform,
        String appVersion,
        IssueSeverity severity,
        Boolean reproducible,
        Instant createdAt,
        Instant updatedAt
) {
}


