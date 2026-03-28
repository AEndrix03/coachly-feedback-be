package it.aredegalli.coachly.feedback.mapper;

import it.aredegalli.coachly.feedback.dto.FeedbackResponse;
import it.aredegalli.coachly.feedback.model.FeedbackEntry;

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


