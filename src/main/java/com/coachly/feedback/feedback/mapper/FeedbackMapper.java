package com.coachly.feedback.feedback.mapper;

import com.coachly.feedback.feedback.api.FeedbackResponse;
import com.coachly.feedback.feedback.domain.FeedbackEntry;

public final class FeedbackMapper {

    private FeedbackMapper() {
    }

    public static FeedbackResponse toResponse(FeedbackEntry entry) {
        return new FeedbackResponse(
                entry.getId(),
                entry.getAuthorUserId(),
                entry.getType(),
                entry.getStatus(),
                entry.getVisibility(),
                entry.getTitle(),
                entry.getBody(),
                entry.getRatingValue(),
                entry.getCategory(),
                entry.getTargetType(),
                entry.getTargetId(),
                entry.getFeatureKey(),
                entry.getScreenKey(),
                entry.getFlowKey(),
                entry.getPlatform(),
                entry.getAppVersion(),
                entry.getSeverity(),
                entry.getReproducible(),
                entry.getCreatedAt(),
                entry.getUpdatedAt()
        );
    }
}