package com.coachly.feedback.feedback.api;

import com.coachly.feedback.common.model.TargetType;

import java.util.Map;
import java.util.UUID;

public record FeedbackSummaryResponse(
        TargetType targetType,
        UUID targetId,
        double averageRating,
        long totalReviews,
        Map<Integer, Long> distribution
) {
}