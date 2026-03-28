package com.coachly.feedback.featurerequest.mapper;

import com.coachly.feedback.featurerequest.api.FeatureRequestResponse;
import com.coachly.feedback.featurerequest.domain.FeatureRequest;

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