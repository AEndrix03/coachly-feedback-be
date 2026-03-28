package it.aredegalli.coachly.feedback.dto;

import it.aredegalli.coachly.feedback.model.TargetType;

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


