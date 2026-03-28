package it.aredegalli.coachly.feedback.mapper;

import it.aredegalli.coachly.feedback.dto.FeatureRequestResponse;
import it.aredegalli.coachly.feedback.model.FeatureRequest;

public final class FeatureRequestMapper {
    private FeatureRequestMapper() {}

    public static FeatureRequestResponse toResponse(FeatureRequest entity) {
        return new FeatureRequestResponse(
                entity.getId(),
                entity.getAuthorUserId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getCategory(),
                entity.getStatus(),
                entity.getDuplicateOfId(),
                entity.getPlatformTarget(),
                entity.getModuleKey(),
                entity.getUpvotesCount(),
                entity.getCommentsCount(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}


