package com.coachly.feedback.feedback.api;

import com.coachly.feedback.common.model.ContentVisibility;
import com.coachly.feedback.common.model.FeedbackStatus;
import com.coachly.feedback.common.model.FeedbackType;
import com.coachly.feedback.common.model.IssueSeverity;
import com.coachly.feedback.common.model.TargetType;

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